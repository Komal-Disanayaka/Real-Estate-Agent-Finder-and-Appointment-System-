<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Agent List</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-5">
    <div class="row">
        <div class="col-12">
            <div class="card">
                <div class="card-header bg-primary text-white">
                    <div class="d-flex justify-content-between align-items-center">
                        <h3 class="mb-0">Real Estate Agents</h3>
                        <div>
                            <a href="/agent/list?sortBy=rating" class="btn btn-sm btn-info me-2">Sort by Rating</a>
                            <c:if test="${not empty sessionScope.user && sessionScope.user.userType eq 'admin'}">
                                <a href="/agent/register" class="btn btn-sm btn-success">Add Agent</a>
                            </c:if>
                        </div>
                    </div>
                </div>
                <div class="card-body">
                    <form action="/agent/search" method="get" class="mb-4">
                        <div class="row g-3">
                            <div class="col-md-4">
                                <label for="name" class="form-label">Name</label>
                                <input type="text" class="form-control" id="name" name="name">
                            </div>
                            <div class="col-md-4">
                                <label for="location" class="form-label">Location</label>
                                <input type="text" class="form-control" id="location" name="location">
                            </div>
                            <div class="col-md-4">
                                <label for="specialization" class="form-label">Specialization</label>
                                <input type="text" class="form-control" id="specialization" name="specialization">
                            </div>
                            <div class="col-12">
                                <button type="submit" class="btn btn-primary">Search</button>
                                <a href="/agent/list" class="btn btn-secondary">Reset</a>
                            </div>
                        </div>
                    </form>

                    <c:if test="${not empty param.registered}">
                        <div class="alert alert-success">Agent registered successfully!</div>
                    </c:if>
                    <c:if test="${not empty param.updated}">
                        <div class="alert alert-success">Agent updated successfully!</div>
                    </c:if>
                    <c:if test="${not empty param.deleted}">
                        <div class="alert alert-success">Agent deleted successfully!</div>
                    </c:if>

                    <div class="table-responsive">
                        <table class="table table-striped table-hover">
                            <thead>
                            <tr>
                                <th>Name</th>
                                <th>Contact</th>
                                <th>Location</th>
                                <th>Specialization</th>
                                <th>Rating</th>
                                <th>Actions</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach var="agent" items="${agents}">
                                <tr>
                                    <td>${agent.name}</td>
                                    <td>${agent.contact}</td>
                                    <td>${agent.location}</td>
                                    <td>${agent.specialization}</td>
                                    <td>
                                        <div class="rating">
                                            <c:forEach begin="1" end="5" var="i">
                                                <span class="star ${i <= agent.rating ? 'text-warning' : 'text-secondary'}">★</span>
                                            </c:forEach>
                                            (${agent.rating})
                                        </div>
                                    </td>
                                    <td>
                                        <div class="d-flex gap-2">
                                            <a href="/agent/view?id=${agent.id}" class="btn btn-sm btn-info">View</a>
                                            <c:if test="${not empty sessionScope.user && sessionScope.user.userType eq 'admin'}">
                                                <a href="/agent/edit?id=${agent.id}" class="btn btn-sm btn-warning">Edit</a>
                                                <form action="/agent/delete" method="post" style="display: inline;">
                                                    <input type="hidden" name="id" value="${agent.id}">
                                                    <button type="submit" class="btn btn-sm btn-danger" onclick="return confirm('Are you sure you want to delete this agent?')">Delete</button>
                                                </form>
                                            </c:if>
                                            <c:if test="${not empty sessionScope.user && sessionScope.user.userType eq 'client'}">
                                                <a href="/appointment/book?agentId=${agent.id}" class="btn btn-sm btn-success">Book Appointment</a>
                                                <a href="/review/add?agentId=${agent.id}" class="btn btn-sm btn-primary">Add Review</a>
                                            </c:if>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>

                    <div class="d-flex justify-content-center mt-3">
                        <a href="/" class="btn btn-outline-secondary">Back to Home</a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>