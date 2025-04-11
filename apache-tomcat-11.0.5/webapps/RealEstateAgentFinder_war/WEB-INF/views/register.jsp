<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Register</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-8">
            <div class="card">
                <div class="card-header bg-primary text-white">
                    <h3 class="text-center">Register</h3>
                </div>
                <div class="card-body">
                    <c:if test="${not empty error}">
                        <div class="alert alert-danger">${error}</div>
                    </c:if>

                    <form action="/user/register" method="post">
                        <div class="mb-3">
                            <label class="form-label">User Type</label>
                            <div class="form-check">
                                <input class="form-check-input" type="radio" name="userType" id="clientType" value="client" checked>
                                <label class="form-check-label" for="clientType">Client</label>
                            </div>
                            <div class="form-check">
                                <input class="form-check-input" type="radio" name="userType" id="agentType" value="agent">
                                <label class="form-check-label" for="agentType">Agent</label>
                            </div>
                        </div>

                        <div class="mb-3">
                            <label for="username" class="form-label">Username</label>
                            <input type="text" class="form-control" id="username" name="username" required>
                        </div>
                        <div class="mb-3">
                            <label for="password" class="form-label">Password</label>
                            <input type="password" class="form-control" id="password" name="password" required>
                        </div>
                        <div class="mb-3">
                            <label for="email" class="form-label">Email</label>
                            <input type="email" class="form-control" id="email" name="email" required>
                        </div>
                        <div class="mb-3">
                            <label for="phone" class="form-label">Phone</label>
                            <input type="tel" class="form-control" id="phone" name="phone" required>
                        </div>

                        <div id="agentFields" style="display: none;">
                            <div class="mb-3">
                                <label for="licenseNumber" class="form-label">License Number</label>
                                <input type="text" class="form-control" id="licenseNumber" name="licenseNumber">
                            </div>
                            <div class="mb-3">
                                <label for="specialization" class="form-label">Specialization</label>
                                <input type="text" class="form-control" id="specialization" name="specialization">
                            </div>
                            <div class="mb-3">
                                <label for="yearsOfExperience" class="form-label">Years of Experience</label>
                                <input type="number" class="form-control" id="yearsOfExperience" name="yearsOfExperience" min="0">
                            </div>
                        </div>

                        <div class="d-grid gap-2">
                            <button type="submit" class="btn btn-primary">Register</button>
                            <a href="/user/login" class="btn btn-secondary">Already have an account? Login</a>
                            <a href="/" class="btn btn-outline-secondary">Back to Home</a>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
    document.addEventListener('DOMContentLoaded', function() {
        const agentTypeRadio = document.getElementById('agentType');
        const agentFields = document.getElementById('agentFields');

        agentTypeRadio.addEventListener('change', function() {
            if (this.checked) {
                agentFields.style.display = 'block';
                // Make agent fields required
                document.getElementById('licenseNumber').required = true;
                document.getElementById('specialization').required = true;
                document.getElementById('yearsOfExperience').required = true;
            }
        });

        document.getElementById('clientType').addEventListener('change', function() {
            if (this.checked) {
                agentFields.style.display = 'none';
                // Make agent fields not required
                document.getElementById('licenseNumber').required = false;
                document.getElementById('specialization').required = false;
                document.getElementById('yearsOfExperience').required = false;
            }
        });
    });
</script>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>