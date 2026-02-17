const API_BASE = "/api/v1/venue-status";
const DEVICE_KEY = "matchday_device_id";

const cityInput = document.getElementById("cityInput");
const venueInput = document.getElementById("venueInput");
const statusInput = document.getElementById("statusInput");
const refreshBtn = document.getElementById("refreshBtn");
const submitBtn = document.getElementById("submitBtn");
const deviceIdLine = document.getElementById("deviceIdLine");
const feedback = document.getElementById("feedback");
const venueList = document.getElementById("venueList");
const venueTemplate = document.getElementById("venueTemplate");
const countBadge = document.getElementById("countBadge");

let currentCity = "Casablanca";

function getDeviceId() {
  let id = localStorage.getItem(DEVICE_KEY);
  if (!id) {
    id = "dev-" + crypto.randomUUID();
    localStorage.setItem(DEVICE_KEY, id);
  }
  return id;
}

function setFeedback(message, type = "info") {
  feedback.textContent = message;
  feedback.dataset.type = type;
}

function formatDate(isoDate) {
  if (!isoDate) return "No update time";
  const d = new Date(isoDate);
  if (Number.isNaN(d.getTime())) return "Unknown time";
  return d.toLocaleString();
}

function statusClass(status) {
  const v = (status || "").toLowerCase();
  if (v === "quiet") return "quiet";
  if (v === "packed") return "packed";
  return "ok";
}

async function fetchStatuses(city) {
  const url = `${API_BASE}?city=${encodeURIComponent(city)}`;
  const response = await fetch(url, { headers: { "Accept": "application/json" } });
  if (!response.ok) {
    throw new Error(`Failed to fetch statuses (${response.status})`);
  }
  return response.json();
}

async function updateVenue(venueId, status) {
  const response = await fetch(`${API_BASE}/${encodeURIComponent(venueId)}`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      "X-Device-Id": getDeviceId()
    },
    body: JSON.stringify({ status })
  });

  const requestId = response.headers.get("X-Request-Id");

  if (response.ok) {
    return { ok: true, body: await response.json(), requestId };
  }

  let payload = {};
  try {
    payload = await response.json();
  } catch (_e) {
    payload = {};
  }

  return {
    ok: false,
    status: response.status,
    retryAfter: response.headers.get("Retry-After"),
    error: payload.error,
    message: payload.message,
    requestId: payload.requestId || requestId
  };
}

function renderVenues(items) {
  venueList.textContent = "";
  countBadge.textContent = String(items.length);

  if (!items.length) {
    const empty = document.createElement("p");
    empty.className = "empty";
    empty.textContent = "No venue statuses found yet. Submit the first update.";
    venueList.appendChild(empty);
    return;
  }

  for (const item of items) {
    const node = venueTemplate.content.firstElementChild.cloneNode(true);
    const idEl = node.querySelector(".venue-id");
    const statusEl = node.querySelector(".status-pill");
    const updatedEl = node.querySelector(".updated-at");
    const chips = node.querySelectorAll(".chip");

    idEl.textContent = item.venueId;
    statusEl.textContent = item.status;
    statusEl.classList.add(statusClass(item.status));
    updatedEl.textContent = `Updated ${formatDate(item.updatedAt)}`;

    chips.forEach((chip) => {
      chip.addEventListener("click", async () => {
        await submitUpdate(item.venueId, chip.dataset.status);
      });
    });

    venueList.appendChild(node);
  }
}

async function refreshList() {
  const city = cityInput.value.trim() || currentCity;
  currentCity = city;
  cityInput.value = city;
  setFeedback(`Loading statuses for ${city}...`);

  try {
    const data = await fetchStatuses(city);
    renderVenues(data.items || []);
    setFeedback(`Loaded ${data.items?.length ?? 0} venues for ${data.city}.`, "success");
  } catch (error) {
    setFeedback(error.message, "error");
  }
}

async function submitUpdate(venueIdOverride, statusOverride) {
  const venueId = (venueIdOverride || venueInput.value || "").trim();
  const status = statusOverride || statusInput.value;

  if (!venueId) {
    setFeedback("Venue ID is required to submit an update.", "error");
    return;
  }

  setFeedback(`Submitting ${status} for ${venueId}...`);
  submitBtn.disabled = true;

  try {
    const result = await updateVenue(venueId, status);
    if (result.ok) {
      venueInput.value = venueId;
      setFeedback(`Updated ${venueId} to ${status}. Request ID: ${result.requestId || "n/a"}`, "success");
      await refreshList();
      return;
    }

    if (result.status === 429) {
      const wait = result.retryAfter || "a bit";
      setFeedback(`Cooldown active for this device on ${venueId}. Retry in ${wait}s.`, "error");
      return;
    }

    setFeedback(
      `${result.message || "Update failed"}${result.requestId ? ` (request ${result.requestId})` : ""}`,
      "error"
    );
  } catch (error) {
    setFeedback(`Unexpected error: ${error.message}`, "error");
  } finally {
    submitBtn.disabled = false;
  }
}

refreshBtn.addEventListener("click", refreshList);
submitBtn.addEventListener("click", async () => submitUpdate());
cityInput.addEventListener("keydown", async (evt) => {
  if (evt.key === "Enter") {
    await refreshList();
  }
});

deviceIdLine.textContent = `Device ID: ${getDeviceId()}`;
cityInput.value = currentCity;
void refreshList();
