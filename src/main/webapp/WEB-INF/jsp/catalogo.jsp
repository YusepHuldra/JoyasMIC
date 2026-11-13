<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="es_MX"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Joyas Mic | Catálogo Digital</title>
    <link rel="stylesheet" href="${ctx}/css/estilos.css">
</head>
<body>

<jsp:include page="/WEB-INF/jspf/flash.jspf"/>
<jsp:include page="/WEB-INF/jspf/cabecera.jspf"/>

<section class="hero" id="inicio">
    <h1>Colección Exclusiva <span>Joyas Mic</span></h1>
    <p>Explora nuestro catálogo digital de piezas finas. Diseños exclusivos en oro y plata pensados para perdurar.</p>
</section>

<main class="catalog-container" id="catalogo">
    <h2 class="catalog-title">Nuestras Piezas</h2>

    <div class="category-tabs">
        <c:forEach var="t" items="${pestanas}">
            <a class="tab-btn ${t == categoriaActual ? 'active' : ''}"
               href="${ctx}/catalogo?categoria=${t}#catalogo">${t == 'Todos' ? 'Ver Todo' : t}</a>
        </c:forEach>
    </div>

    <div class="grid-products" id="grid-products-container">
        <c:if test="${empty productos}">
            <p style="color:#64748b; text-align:center; grid-column: 1/-1; margin-top:2rem;">
                No hay piezas disponibles en el apartado de <c:out value="${categoriaActual}"/>.
            </p>
        </c:if>

        <c:forEach var="p" items="${productos}">
            <div class="product-card">
                <c:if test="${sessionScope.usuario.admin}">
                    <form method="post" action="${ctx}/admin/eliminar"
                          onsubmit="return confirm('¿Seguro que deseas eliminar esta pieza del catálogo?');">
                        <input type="hidden" name="id" value="${p.id}">
                        <input type="hidden" name="volver" value="catalogo">
                        <input type="hidden" name="categoria" value="${categoriaActual}">
                        <button type="submit" class="btn-delete-prod" style="display:block;">Eliminar</button>
                    </form>
                </c:if>

                <div class="product-image">
                    <img src="${ctx}/imagenes/<c:out value='${p.imagen}'/>" alt="<c:out value='${p.nombre}'/>"
                         onerror="this.onerror=null;this.src='${ctx}/img/placeholder.svg'">
                </div>
                <div class="product-info">
                    <div>
                        <div class="product-category"><c:out value="${p.categoria}"/></div>
                        <h3 class="product-name"><c:out value="${p.nombre}"/></h3>
                        <p style="color:#d4af37; font-weight:700; font-size:1.1rem; margin-bottom: 0.5rem;">
                            Precio: $<fmt:formatNumber value="${p.precio}" pattern="#,##0.##"/>
                        </p>
                        <p class="product-details"><c:out value="${p.detalles}"/></p>
                    </div>
                    <form method="post" action="${ctx}/carrito/agregar">
                        <input type="hidden" name="id" value="${p.id}">
                        <input type="hidden" name="categoria" value="${categoriaActual}">
                        <button type="submit" class="btn-consult btn-add-to-cart">Agregar al Carrito</button>
                    </form>
                </div>
            </div>
        </c:forEach>
    </div>
</main>

<jsp:include page="/WEB-INF/jspf/carrito.jspf"/>
<jsp:include page="/WEB-INF/jspf/modal-auth.jspf"/>
<jsp:include page="/WEB-INF/jspf/pie.jspf"/>

</body>
</html>
