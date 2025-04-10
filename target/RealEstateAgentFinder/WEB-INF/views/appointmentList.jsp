<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>My Appointments</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-5">
    <div class="card">
        <div class="card-header bg-primary text-white">
            <h3 class="mb-0">My Appointments</h3>
        </div>
        <div class="card-body">
            <!-- Filter Section -->
            <form action="${pageContext.request.contextPath}/appointment/filter" method="get" class="mb-4">
                <div class="row g-3">
                    <div class="col-md-6">
                        <label for="status" class="form-label">Status</label>
                        <select class="form-select" id="status" name="status">
                            <option value="">All Status</option>
                            <option value="Scheduled" ${param.status eq 'Scheduled' ? 'selected' : ''}>Scheduled</option>
                            <option value="Confirmed" ${param.status eq 'Confirmed' ? 'selected' : ''}>Confirmed</option>
                            <option value="Completed" ${param.status eq 'Completed' ? 'selected' : ''}>Completed</option>
                            <option value="Cancelled" ${param.status eq 'Cancelled' ? 'selected' : ''}>Cancelled</option>
                        </select>
                    </div>
                    <div class="col-md-6">
                        <label for="date" class="form-label">Date</label>
                        <input type="date" class="form-control" id="date" name="date" value="${param.date}">
                    </div>
                    <div class="col-12">
                        <button type="submit" class="btn btn-primary">Filter</button>
                        <a href="${pageContext.request.contextPath}/appointment/list" class="btn btn-secondary">Reset</a>
                    </div>
                </div>
            </form>

            <!-- Appointments Table -->
            <div class="table-responsive">
                <table class="table table-striped table-hover">
                    <thead>
                        <tr>
                            <th>Date & Time</th>
                            <th>Client Name</th>
                            <th>Property</th>
                            <th>Status</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="appointment" items="${appointments}">
                            <tr>
                                <td>${appointment.appointmentTime}</td>
                                <td>Client ${appointment.clientId}</td>
                                <td>Property ${appointment.propertyId}</td>
                                <td>
                                    <span class="badge bg-${appointment.status eq 'Scheduled' ? 'warning' : 
                                                        appointment.status eq 'Confirmed' ? 'success' : 
                                                        appointment.status eq 'Completed' ? 'info' : 'danger'}">
                                        ${appointment.status}
                                    </span>
                                </td>
                                <td>
                                    <div class="btn-group" role="group">
                                        <button type="button" class="btn btn-sm btn-info" data-bs-toggle="modal" 
                                                data-bs-target="#viewModal${appointment.id}">View</button>
                                        <c:if test="${appointment.status eq 'Scheduled'}">
                                            <form action="${pageContext.request.contextPath}/appointment/confirm" method="post" style="display: inline;">
                                                <input type="hidden" name="id" value="${appointment.id}">
                                                <button type="submit" class="btn btn-sm btn-success">Confirm</button>
                                            </form>
                                        </c:if>
                                        <c:if test="${appointment.status eq 'Confirmed'}">
                                            <form action="${pageContext.request.contextPath}/appointment/complete" method="post" style="display: inline;">
                                                <input type="hidden" name="id" value="${appointment.id}">
                                                <button type="submit" class="btn btn-sm btn-primary">Complete</button>
                                            </form>
                                        </c:if>
                                        <c:if test="${appointment.status ne 'Completed' && appointment.status ne 'Cancelled'}">
                                            <button type="button" class="btn btn-sm btn-danger" data-bs-toggle="modal" 
                                                    data-bs-target="#cancelModal${appointment.id}">Cancel</button>
                                        </c:if>
                                    </div>
                                </td>
                            </tr>

                            <!-- View Modal -->
                            <div class="modal fade" id="viewModal${appointment.id}" tabindex="-1">
                                <div class="modal-dialog">
                                    <div class="modal-content">
                                        <div class="modal-header">
                                            <h5 class="modal-title">Appointment Details</h5>
                                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                        </div>
                                        <div class="modal-body">
                                            <p><strong>Date & Time:</strong> ${appointment.appointmentTime}</p>
                                            <p><strong>Client ID:</strong> ${appointment.clientId}</p>
                                            <p><strong>Property ID:</strong> ${appointment.propertyId}</p>
                                            <p><strong>Purpose:</strong> ${appointment.purpose}</p>
                                            <p><strong>Status:</strong> ${appointment.status}</p>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <!-- Cancel Modal -->
                            <div class="modal fade" id="cancelModal${appointment.id}" tabindex="-1">
                                <div class="modal-dialog">
                                    <div class="modal-content">
                                        <div class="modal-header">
                                            <h5 class="modal-title">Cancel Appointment</h5>
                                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                        </div>
                                        <div class="modal-body">
                                            <p>Are you sure you want to cancel this appointment?</p>
                                        </div>
                                        <div class="modal-footer">
                                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">No</button>
                                            <form action="${pageContext.request.contextPath}/appointment/cancel" method="post">
                                                <input type="hidden" name="id" value="${appointment.id}">
                                                <button type="submit" class="btn btn-danger">Yes, Cancel</button>
                                            </form>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </tbody>
                </table>
            </div>

            <div class="d-flex justify-content-center mt-3">
                <a href="${pageContext.request.contextPath}/" class="btn btn-outline-secondary">Back to Home</a>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html> 