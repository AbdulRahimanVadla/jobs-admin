console.log("✅ jobForm.js loaded inside modal");

// Detect API base depending on environment
// 👇 Always point to your deployed backend
const API_BASE = "https://jobs-admin-production.up.railway.app";
  // Railway or any deployed host

// Function to attach submit handler once form exists
const tryAttach = () => {
  const jobForm = document.getElementById("jobForm");
  if (!jobForm) {
    console.log("⚠️ jobForm not found yet, retrying...");
    setTimeout(tryAttach, 200);
    return;
  }

  console.log("🎯 jobForm found, attaching submit handler");

  jobForm.addEventListener("submit", async (e) => {
    e.preventDefault();
    console.log("📩 Form intercepted by JS");

    // Collect form values
    const formData = {
      title: document.getElementById("title").value,
      companyName: document.getElementById("companyName").value,
      location: document.getElementById("location").value,
      jobType: document.getElementById("jobType").value,
      salaryMin: jobForm.querySelector("input[name='salaryMin']").value || null,
      salaryMax: jobForm.querySelector("input[name='salaryMax']").value || null,
      applicationDeadline: document.getElementById("applicationDeadline").value || null,
      description: document.getElementById("description").value,
      // extra backend fields
      requirements: "",
      responsibilities: ""
    };

    try {
      const res = await fetch(API_BASE + "/api/jobs", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(formData),
      });

      const data = await res.json();
      console.log("✅ Server response:", data);

      if (res.ok) {
        alert("Job saved successfully!");

        // Add job card dynamically to grid
        const card = document.createElement("article");
        card.className = "card";
        card.innerHTML = `
          <div class="card-top">
            <div class="company-logo"><img src="./pics/default.png" alt=""></div>
            <span class="ago">Now</span>
          </div>
          <h3>${formData.title}</h3>
          <div class="meta">
            <span>🏢 ${formData.companyName}</span>
            <span>📍 ${formData.location}</span>
            <span>👤 ${formData.jobType}</span>
            <span>💰 ₹${formData.salaryMin || 0} - ₹${formData.salaryMax || 0}</span>
          </div>
          <ul class="desc">
            <li>${formData.description}</li>
          </ul>
          ${formData.applicationDeadline ? `<p class="deadline">Apply by: ${formData.applicationDeadline}</p>` : ""}
          <button class="apply">Apply Now</button>
        `;

        const grid = window.parent.document.querySelector(".grid");
        if (grid) {
          grid.prepend(card);
          console.log("✅ Job card added to grid");
        }

        // Close modal
        const modal = document.getElementById("jobModal");
        if (modal) {
          modal.classList.remove("active");
          console.log("❌ Modal closed");
        }
      } else {
        alert("Error: " + (data.error || "Unknown error"));
      }
    } catch (err) {
      console.error("❌ Failed:", err);
      alert("Could not save job. Check console for details.");
    }
  });
};

// Start trying to attach handler
tryAttach();
