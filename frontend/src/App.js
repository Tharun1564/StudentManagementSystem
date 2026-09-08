import React, { useState, useEffect } from "react";
import "./App.css";

const API_URL = "http://localhost:8081/StudentManagementPortal/students";

const MONOGRAM_COLORS = ["#7C5CFF", "#FF6B5B", "#00B894", "#0984E3", "#E17055", "#6C5CE7"];
const BADGE_COLORS = [
  { bg: "#E3F7EE", text: "#00A876" },
  { bg: "#FFF3D6", text: "#B8860B" },
  { bg: "#E6F0FF", text: "#3366CC" },
  { bg: "#FFE4E8", text: "#D63C6E" },
  { bg: "#F0EBFF", text: "#7C5CFF" },
];

function getInitials(name) {
  const parts = name.trim().split(" ").filter(Boolean);
  if (parts.length === 0) return "?";
  if (parts.length === 1) return parts[0][0].toUpperCase();
  return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
}

function colorFromString(str, palette) {
  let hash = 0;
  for (let i = 0; i < str.length; i++) hash = str.charCodeAt(i) + ((hash << 5) - hash);
  return palette[Math.abs(hash) % palette.length];
}

function App() {
  const [students, setStudents] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [form, setForm] = useState({ name: "", email: "", course: "", age: "" });
  const [errors, setErrors] = useState({});
  const [editingId, setEditingId] = useState(null);
  const [serverError, setServerError] = useState("");

  const fetchStudents = () => {
    fetch(API_URL)
      .then((res) => res.json())
      .then((data) => setStudents(data))
      .catch((err) => console.error("Error fetching students:", err));
  };

  useEffect(() => {
    fetchStudents();
  }, []);

  const validate = () => {
    const newErrors = {};
    if (!form.name.trim()) newErrors.name = "Name is required";
    else if (form.name.trim().length < 2) newErrors.name = "Name must be at least 2 characters";

    if (!form.email.trim()) newErrors.email = "Email is required";
    else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) newErrors.email = "Enter a valid email address";

    if (!form.course.trim()) newErrors.course = "Course is required";

    if (!form.age) newErrors.age = "Age is required";
    else if (isNaN(form.age) || form.age < 15 || form.age > 100) newErrors.age = "Age must be between 15 and 100";

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
    if (errors[e.target.name]) setErrors({ ...errors, [e.target.name]: null });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    setServerError("");
    if (!validate()) return;

    const payload = { ...form, age: parseInt(form.age) };
    const isEditing = !!editingId;

    fetch(isEditing ? `${API_URL}/${editingId}` : API_URL, {
      method: isEditing ? "PUT" : "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload),
    })
      .then(async (res) => {
        if (!res.ok) {
          const errBody = await res.json().catch(() => ({}));
          throw new Error(errBody.error || "Something went wrong. Please try again.");
        }
        return res.json();
      })
      .then(() => {
        setEditingId(null);
        setForm({ name: "", email: "", course: "", age: "" });
        setErrors({});
        fetchStudents();
      })
      .catch((err) => setServerError(err.message));
  };

  const handleEdit = (student) => {
    setForm({ name: student.name, email: student.email, course: student.course, age: student.age });
    setErrors({});
    setServerError("");
    setEditingId(student.id);
  };

  const handleCancelEdit = () => {
    setEditingId(null);
    setForm({ name: "", email: "", course: "", age: "" });
    setErrors({});
    setServerError("");
  };

  const handleDelete = (id) => {
    if (!window.confirm("Remove this student record?")) return;
    fetch(`${API_URL}/${id}`, { method: "DELETE" }).then(() => fetchStudents());
  };

  // Fixed: now checks name, email, course, AND age (as a string match)
  const filteredStudents = students.filter((s) => {
    const term = searchTerm.trim().toLowerCase();
    if (!term) return true;
    return (
      s.name.toLowerCase().includes(term) ||
      s.email.toLowerCase().includes(term) ||
      s.course.toLowerCase().includes(term) ||
      String(s.age).includes(term)
    );
  });

  return (
    <div className="portal">
      <div className="portal-header">
        <div>
          <h1>Student Register</h1>
          <p>Enrollment records maintained by the department</p>
        </div>
        <div className="stat-block">
          <div className="stat-number">{students.length}</div>
          <div className="stat-label">ON FILE</div>
        </div>
      </div>

      <div className="entry-slip">
        <div className="entry-slip-label">
          {editingId ? `EDITING RECORD #${editingId}` : "NEW ENTRY"}
        </div>

        {serverError && <div className="form-error-banner">{serverError}</div>}

        <form onSubmit={handleSubmit} noValidate>
          <div className="field">
            <label>Name</label>
            <input name="name" value={form.name} onChange={handleChange} />
            {errors.name && <span className="field-error">{errors.name}</span>}
          </div>
          <div className="field">
            <label>Email</label>
            <input name="email" value={form.email} onChange={handleChange} />
            {errors.email && <span className="field-error">{errors.email}</span>}
          </div>
          <div className="field">
            <label>Course</label>
            <input name="course" value={form.course} onChange={handleChange} />
            {errors.course && <span className="field-error">{errors.course}</span>}
          </div>
          <div className="field">
            <label>Age</label>
            <input name="age" value={form.age} onChange={handleChange} />
            {errors.age && <span className="field-error">{errors.age}</span>}
          </div>
          <button type="submit" className="btn-primary">
            {editingId ? "Save" : "Enroll"}
          </button>
        </form>
        {editingId && (
          <div style={{ marginTop: 10 }}>
            <button className="icon-btn edit" onClick={handleCancelEdit}>Cancel edit</button>
          </div>
        )}
      </div>

      <div className="search-row">
        <input
          className="search-input"
          type="text"
          placeholder="Search by name, email, course, or age..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
        />
        {searchTerm && (
          <span className="search-count">{filteredStudents.length} of {students.length} shown</span>
        )}
      </div>

      <div className="ledger-wrap">
        <table className="ledger">
          <thead>
            <tr>
              <th>ID</th>
              <th>Name</th>
              <th>Email</th>
              <th>Course</th>
              <th>Age</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {filteredStudents.map((s) => {
              const monoColor = colorFromString(s.name, MONOGRAM_COLORS);
              const badgeColor = colorFromString(s.course, BADGE_COLORS);
              return (
                <tr key={s.id}>
                  <td><span className="id-badge">#{s.id}</span></td>
                  <td>
                    <div className="name-cell">
                      <div className="monogram" style={{ background: monoColor }}>
                        {getInitials(s.name)}
                      </div>
                      {s.name}
                    </div>
                  </td>
                  <td>{s.email}</td>
                  <td>
                    <span className="course-badge" style={{ background: badgeColor.bg, color: badgeColor.text }}>
                      {s.course}
                    </span>
                  </td>
                  <td>{s.age}</td>
                  <td>
                    <div className="row-actions">
                      <button className="icon-btn edit" onClick={() => handleEdit(s)}>Edit</button>
                      <button className="icon-btn delete" onClick={() => handleDelete(s.id)}>Delete</button>
                    </div>
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>

        {students.length === 0 && (
          <div className="empty-state">
            <div className="empty-state-mark">🎓</div>
            No students enrolled yet — add the first record above.
          </div>
        )}
        {students.length > 0 && filteredStudents.length === 0 && (
          <div className="empty-state">No records match "{searchTerm}"</div>
        )}
      </div>
    </div>
  );
}

export default App;
