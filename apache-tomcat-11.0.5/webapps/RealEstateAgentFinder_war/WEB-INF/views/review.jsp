<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Reviews</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-5">
    <div class="row">
        <div class="col-md-8 offset-md-2">
            <div class="card">
                <div class="card-header bg-primary text-white">
                    <h3 class="mb-0">Reviews for ${agent.name}</h3>
                </div>
                <div class="card-body">
                    <div class="text-center mb-4">
                        <h4>Average Rating: ${averageRating}</h4>
                        <div class="rating mb-2">
                            <c:forEach begin="1" end="5" var="i">
                                <span class="star ${i <= averageRating ? 'text-warning' : 'text-secondary'}">★</span>
                            </c:forEach>
                            <span>(${reviews.size()} reviews)</span>
                        </div>
                        <c:if test="${not empty sessionScope.user && sessionScope.user.userType eq 'client'}">
                            <a href="/review/add?agentId=${agent.id}" class="btn btn-primary">Add Review</a>
                        </c:if>
                    </div>

                    <c:if test="${not empty param.added}">
                        <div class="alert alert-success">Review added successfully!</div>
                    </c:if>
                    <c:if test="${not empty param.updated}">
                        <div class="alert alert-success">Review updated successfully!</div>
                    </c:if>
                    <c:if test="${not empty param.deleted}">
                        <div class="alert alert-success">Review deleted successfully!</div>
                    </c:if>

                    <div class="list-group">
                        <c:forEach var="review" items="${reviews}">
                            <div class="list-group-item mb-3">
                                <div class="d-flex justify-content-between">
                                    <h5>${review.client.username}</h5>
                                    <div class="rating">
                                        <c:forEach begin="1" end="5" var="i">
                                            <span class="star ${i <= review.review.rating ? 'text-warning' : 'text-secondary'}">★</span>
                                        </c:forEach>
                                        <small class="text-muted">${review.review.reviewDate}</small>
                                    </div>
                                </div>
                                <p class="mt-2">${review.review.comment}</p>

                                <c:if test="${not empty sessionScope.user &&
                                    (sessionScope.user.userType eq 'admin' ||
                                    (sessionScope.user.userType eq 'client' && sessionScope.user.id eq review.review.clientId))}">
                                    <div class="d-flex justify-content-end gap-2">
                                        <c:if test="${sessionScope.user.id eq review.review.clientId}">
                                            <a href="/review/edit?id=${review.review.id}" class="btn btn-sm btn-warning">Edit</a>
                                        </c:if>
                                        <form action="/review/delete" method="post" style="display: inline;">
                                            <input type="hidden" name="id" value="${review.review.id}">
                                            <button type="submit" class="btn btn-sm btn-danger" onclick="return confirm('Are you sure you want to delete this review?')">Delete</button>
                                        </form>
                                    </div>
                                </c:if>
                            </div>
                        </c:forEach>
                    </div>

                    <div class="d-flex justify-content-center mt-3">
                        <a href="/agent/view?id=${agent.id}" class="btn btn-outline-secondary">Back to Agent</a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>