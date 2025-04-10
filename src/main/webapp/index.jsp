<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Real Estate Agent Finder</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-5">
    <div class="row">
        <div class="col-md-8 offset-md-2">
            <div class="card">
                <div class="card-header bg-primary text-white">
                    <h2 class="text-center">Welcome to Real Estate Agent Finder</h2>
                </div>
                <div class="card-body">
                    <c:if test="${not empty param.registered}">
                        <div class="alert alert-success">Registration successful! Please login.</div>
                    </c:if>
                    <c:if test="${not empty param.accountDeleted}">
                        <div class="alert alert-info">Your account has been deleted successfully.</div>
                    </c:if>
                    <c:if test="${not empty param.loggedOut}">
                        <div class="alert alert-info">You have been successfully logged out.</div>
                    </c:if>

                    <div class="text-center mb-4">
                        <c:choose>
                            <c:when test="${not empty sessionScope.user}">
                                <p>Welcome, ${sessionScope.user.username}!</p>
                                <div class="d-flex justify-content-center gap-3">
                                    <a href="${pageContext.request.contextPath}/user/profile" class="btn btn-primary">My Profile</a>
                                    <c:if test="${sessionScope.user.userType eq 'client'}">
                                        <a href="${pageContext.request.contextPath}/agent/list" class="btn btn-success">Find Agents</a>
                                        <a href="${pageContext.request.contextPath}/property/list" class="btn btn-info">View Properties</a>
                                    </c:if>
                                    <c:if test="${sessionScope.user.userType eq 'agent'}">
                                        <a href="${pageContext.request.contextPath}/property/list" class="btn btn-info">Manage Properties</a>
                                        <a href="${pageContext.request.contextPath}/appointment/list" class="btn btn-warning">My Appointments</a>
                                    </c:if>
                                    <a href="${pageContext.request.contextPath}/user/logout" class="btn btn-danger">Logout</a>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="d-flex justify-content-center gap-3">
                                    <a href="${pageContext.request.contextPath}/user/login" class="btn btn-primary">Login</a>
                                    <a href="${pageContext.request.contextPath}/user/register" class="btn btn-secondary">Register</a>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <div class="row mt-4">
                        <div class="col-md-6">
                            <div class="card h-100">
                                <div class="card-header bg-info text-white">
                                    <h3 class="card-title">Find Your Perfect Agent</h3>
                                </div>
                                <div class="card-body">
                                    <p class="card-text">Search through our database of professional real estate agents.</p>
                                    <a href="${pageContext.request.contextPath}/agent/list" class="btn btn-info">Browse Agents</a>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="card h-100">
                                <div class="card-header bg-success text-white">
                                    <h3 class="card-title">Property Listings</h3>
                                </div>
                                <div class="card-body">
                                    <p class="card-text">View our latest property listings in your area.</p>
                                    <a href="${pageContext.request.contextPath}/property/list" class="btn btn-success">View Properties</a>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html> 