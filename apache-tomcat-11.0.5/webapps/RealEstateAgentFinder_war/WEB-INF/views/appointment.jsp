<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Appointments</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-5">
    <div class="row">
        <div class="col-12">
            <div class="card">
                <div class="card-header bg-primary text-white">
                    <h3 class="mb-0">My Appointments</h3>
                </div>
                <div class="card-body">
                    <c:if test="${not empty param.booked}">
                        <div class="alert alert-success">Appointment booked successfully!</div>
                    </c:if>
                    <c:if test="${not empty param.updated}">
                        <div class="alert alert-success">Appointment updated successfully!</div>
                    </c:if>
                    <c:if test="${not empty param.cancelled}">
                        <div class="alert alert-success">Appointment cancelled successfully!</div>
                    </c:if>

                    <div class="table-responsive">
                        <table class="table table-striped">
                            <thead>
                            <tr>
                                <th>Date & Time</th>
                                <th>Property</th>
                                <c:if test="${isAgent}">
                                    <th>Client</th>
                                </c:if>
                                <c:if test="${not isAgent}">
                                    <th>Agent</th>
                                </c:if>
                                <th>Purpose</th>
                                <th>Status</th>
                                <th>Actions</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach var="appt" items="${appointments}">
                                <tr>
                                    <td>${appt.appointment.appointmentTime}</td>
                                    <td>
                                        <a href="/property/view?id=${appt.appointment.propertyId}">
                                                ${appt.property.address}
                                        </a>
                                    </td>
                                    <c:if test="${isAgent}">
                                        <td>${appt.client.username}</td>
                                    </c:if>
                                    <c:if test="${not isAgent}">
                                        <td>${appt.agent.name}</td>
                                    </c:if>
                                    <td>${appt.appointment.purpose}</td>
                                    <td>
                                            <span class="badge ${appt.appointment.status eq 'Scheduled' ? 'bg-primary' :
                                                              appt.appointment.status eq 'Completed' ? 'bg-success' : 'bg-danger'}">
                                                    ${appt.appointment.status}
                                            </span>
                                    </td>
                                    <td>
                                        <div class="d-flex gap-2">
                                            <a href="/appointment/view?id=${appt.appointment.id}" class="btn btn-sm btn-info">View</a>
                                            <c:if test="${appt.appointment.status eq 'Scheduled'}">
                                                <a href="/appointment/edit?id=${appt.appointment.id}" class="btn btn-sm btn-warning">Reschedule</a>
                                                <form action="/appointment/cancel" method="post" style="display: inline;">
                                                    <input type="hidden" name="id" value="${appt.appointment.id}">
                                                    <button type="submit" class="btn btn-sm btn-danger" onclick="return confirm('Are you sure you want to cancel this appointment?')">Cancel</button>
                                                </form>
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