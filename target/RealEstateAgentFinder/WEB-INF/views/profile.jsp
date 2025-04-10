<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>My Profile</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-8">
            <div class="card">
                <div class="card-header bg-primary text-white">
                    <h3 class="text-center">My Profile</h3>
                </div>
                <div class="card-body">
                    <c:if test="${not empty success}">
                        <div class="alert alert-success">${success}</div>
                    </c:if>
                    <c:if test="${not empty error}">
                        <div class="alert alert-danger">${error}</div>
                    </c:if>

                    <form action="/user/update" method="post">
                        <div class="mb-3">
                            <label class="form-label">User Type</label>
                            <input type="text" class="form-control" value="${user.userType}" readonly>
                        </div>
                        <div class="mb-3">
                            <label for="username" class="form-label">Username</label>
                            <input type="text" class="form-control" id="username" name="username" value="${user.username}" readonly>
                        </div>
                        <div class="mb-3">
                            <label for="email" class="form-label">Email</label>
                            <input type="email" class="form-control" id="email" name="email" value="${user.email}" required>
                        </div>
                        <div class="mb-3">
                            <label for="phone" class="form-label">Phone</label>
                            <input type="tel" class="form-control" id="phone" name="phone" value="${user.phone}" required>
                        </div>

                        <c:if test="${user.userType eq 'client'}">
                            <div class="mb-3">
                                <label for="preferences" class="form-label">Preferences</label>
                                <input type="text" class="form-control" id="preferences" name="preferences" value="${user.preferences}">
                            </div>
                        </c:if>

                        <c:if test="${user.userType eq 'agent'}">
                            <div class="mb-3">
                                <label for="licenseNumber" class="form-label">License Number</label>
                                <input type="text" class="form-control" id="licenseNumber" name="licenseNumber" value="${user.licenseNumber}" readonly>
                            </div>
                            <div class="mb-3">
                                <label for="specialization" class="form-label">Specialization</label>
                                <input type="text" class="form-control" id="specialization" name="specialization" value="${user.specialization}" required>
                            </div>
                            <div class="mb-3">
                                <label for="yearsOfExperience" class="form-label">Years of Experience</label>
                                <input type="number" class="form-control" id="yearsOfExperience" name="yearsOfExperience" value="${user.yearsOfExperience}" min="0" required>
                            </div>
                        </c:if>

                        <div class="d-grid gap-2">
                            <button type="submit" class="btn btn-primary">Update Profile</button>
                            <button type="button" class="btn btn-danger" data-bs-toggle="modal" data-bs-target="#deleteModal">Delete Account</button>
                            <a href="/" class="btn btn-outline-secondary">Back to Home</a>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Delete Confirmation Modal -->
<div class="modal fade" id="deleteModal" tabindex="-1" aria-labelledby="deleteModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="deleteModalLabel">Confirm Account Deletion</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                Are you sure you want to delete your account? This action cannot be undone.
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                <form action="/user/delete" method="post" style="display: inline;">
                    <button type="submit" class="btn btn-danger">Delete Account</button>
                </form>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>