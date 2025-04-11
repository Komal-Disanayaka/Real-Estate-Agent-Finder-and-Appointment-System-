<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Property Listings</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-5">
    <div class="row">
        <div class="col-12">
            <div class="card">
                <div class="card-header bg-primary text-white">
                    <div class="d-flex justify-content-between align-items-center">
                        <h3 class="mb-0">Property Listings</h3>
                        <div>
                            <c:if test="${not empty sessionScope.user && (sessionScope.user.userType eq 'admin' || sessionScope.user.userType eq 'agent')}">
                                <a href="/property/add" class="btn btn-sm btn-success">Add Property</a>
                            </c:if>
                        </div>
                    </div>
                </div>
                <div class="card-body">
                    <form action="/property/search" method="get" class="mb-4">
                        <div class="row g-3">
                            <div class="col-md-3">
                                <label for="location" class="form-label">Location</label>
                                <input type="text" class="form-control" id="location" name="location">
                            </div>
                            <div class="col-md-3">
                                <label for="type" class="form-label">Property Type</label>
                                <select class="form-select" id="type" name="type">
                                    <option value="">All Types</option>
                                    <option value="house">House</option>
                                    <option value="apartment">Apartment</option>
                                    <option value="land">Land</option>
                                </select>
                            </div>
                            <div class="col-md-3">
                                <label for="minPrice" class="form-label">Min Price</label>
                                <input type="number" class="form-control" id="minPrice" name="minPrice" min="0">
                            </div>
                            <div class="col-md-3">
                                <label for="maxPrice" class="form-label">Max Price</label>
                                <input type="number" class="form-control" id="maxPrice" name="maxPrice" min="0">
                            </div>
                            <div class="col-12">
                                <button type="submit" class="btn btn-primary">Search</button>
                                <a href="/property/list" class="btn btn-secondary">Reset</a>
                            </div>
                        </div>
                    </form>

                    <c:if test="${not empty param.added}">
                        <div class="alert alert-success">Property added successfully!</div>
                    </c:if>
                    <c:if test="${not empty param.updated}">
                        <div class="alert alert-success">Property updated successfully!</div>
                    </c:if>
                    <c:if test="${not empty param.deleted}">
                        <div class="alert alert-success">Property deleted successfully!</div>
                    </c:if>

                    <div class="row">
                        <c:forEach var="property" items="${properties}">
                            <div class="col-md-4 mb-4">
                                <div class="card h-100">
                                    <div class="card-header bg-info text-white">
                                        <h5 class="card-title">${property.address}</h5>
                                    </div>
                                    <div class="card-body">
                                        <p class="card-text">${property.description}</p>
                                        <ul class="list-group list-group-flush mb-3">
                                            <li class="list-group-item"><strong>Price:</strong> $${property.price}</li>
                                            <li class="list-group-item"><strong>Status:</strong> ${property.status}</li>

                                            <c:choose>
                                                <c:when test="${property.getClass().simpleName eq 'House'}">
                                                    <li class="list-group-item"><strong>Type:</strong> House</li>
                                                    <li class="list-group-item"><strong>Bedrooms:</strong> ${property.bedrooms}</li>
                                                    <li class="list-group-item"><strong>Bathrooms:</strong> ${property.bathrooms}</li>
                                                    <li class="list-group-item"><strong>Square Footage:</strong> ${property.squareFootage} sqft</li>
                                                </c:when>
                                                <c:when test="${property.getClass().simpleName eq 'Apartment'}">
                                                    <li class="list-group-item"><strong>Type:</strong> Apartment</li>
                                                    <li class="list-group-item"><strong>Unit #:</strong> ${property.unitNumber}</li>
                                                    <li class="list-group-item"><strong>Bedrooms:</strong> ${property.bedrooms}</li>
                                                    <li class="list-group-item"><strong>Bathrooms:</strong> ${property.bathrooms}</li>
                                                    <li class="list-group-item"><strong>Parking:</strong> ${property.hasParking ? 'Yes' : 'No'}</li>
                                                </c:when>
                                                <c:when test="${property.getClass().simpleName eq 'Land'}">
                                                    <li class="list-group-item"><strong>Type:</strong> Land</li>
                                                    <li class="list-group-item"><strong>Acreage:</strong> ${property.acreage} acres</li>
                                                    <li class="list-group-item"><strong>Zoning:</strong> ${property.zoningType}</li>
                                                </c:when>
                                            </c:choose>
                                        </ul>
                                    </div>
                                    <div class="card-footer">
                                        <div class="d-flex justify-content-between">
                                            <a href="/property/view?id=${property.id}" class="btn btn-sm btn-info">View Details</a>
                                            <c:if test="${not empty sessionScope.user && (sessionScope.user.userType eq 'admin' || sessionScope.user.userType eq 'agent')}">
                                                <div class="btn-group">
                                                    <a href="/property/edit?id=${property.id}" class="btn btn-sm btn-warning">Edit</a>
                                                    <form action="/property/delete" method="post" style="display: inline;">
                                                        <input type="hidden" name="id" value="${property.id}">
                                                        <button type="submit" class="btn btn-sm btn-danger" onclick="return confirm('Are you sure you want to delete this property?')">Delete</button>
                                                    </form>
                                                </div>
                                            </c:if>
                                            <c:if test="${not empty sessionScope.user && sessionScope.user.userType eq 'client'}">
                                                <a href="/appointment/book?propertyId=${property.id}" class="btn btn-sm btn-success">Book Viewing</a>
                                            </c:if>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
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