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
    <title>Panel Administrador | Joyas Mic</title>
    <link rel="stylesheet" href="${ctx}/css/estilos.css">
</head>
<body>

<jsp:include page="/WEB-INF/jspf/flash.jspf"/>

<div class="top-bar">
    <div class="user-nav-status">
        <span>👤 <c:out value="${sessionScope.usuario.nombre}"/> (Administrador)</span>
        <a class="btn-user-action" href="${ctx}/catalogo">← Volver al catálogo</a>
    </div>
    <form class="inline-form" method="post" action="${ctx}/auth/logout">
        <button type="submit" class="btn-user-action btn-logout-user">Cerrar Sesión</button>
    </form>
</div>

<section class="admin-section visible" id="admin-panel">
    <div class="admin-container wide">
        <div class="admin-header-flex">
            <h2 class="catalog-title" style="margin-bottom:1.5rem; text-align:left;">Panel Administrador</h2>
        </div>

        <form id="admin-form" method="post" action="${ctx}/admin/publicar" enctype="multipart/form-data">
            <div class="admin-form-group">
                <label>Categoría / Apartado</label>
                <select name="categoria" required>
                    <c:forEach var="cat" items="${categorias}">
                        <option value="${cat}">${cat}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="admin-form-group">
                <label>Título del Producto</label>
                <input type="text" name="nombre" maxlength="150" placeholder="Ej. Anillo Corona Real" required>
            </div>
            <div class="admin-form-group">
                <label>Precio ($)</label>
                <input type="number" name="precio" min="0.01" step="0.01" placeholder="Ej. 450" required>
            </div>
            <div class="admin-form-group">
                <label>Descripción / Detalles</label>
                <textarea name="detalles" rows="3" placeholder="Describe los materiales, kilataje..." required></textarea>
            </div>
            <div class="admin-form-group">
                <label>Imagen del Producto (máx. 5 MB)</label>
                <input type="file" name="imagen" accept="image/png,image/jpeg,image/gif,image/webp" required>
            </div>
            <button type="submit" class="btn-save-product">Publicar en Catálogo</button>
        </form>

        <table class="admin-table">
            <thead>
            <tr><th></th><th>Producto</th><th>Categoría</th><th>Precio</th><th></th></tr>
            </thead>
            <tbody>
            <c:forEach var="p" items="${productos}">
                <tr>
                    <td><img src="${ctx}/imagenes/<c:out value='${p.imagen}'/>" alt=""
                             onerror="this.onerror=null;this.src='${ctx}/img/placeholder.svg'"></td>
                    <td><c:out value="${p.nombre}"/></td>
                    <td><c:out value="${p.categoria}"/></td>
                    <td>$<fmt:formatNumber value="${p.precio}" pattern="#,##0.##"/></td>
                    <td>
                        <form class="inline-form" method="post" action="${ctx}/admin/eliminar"
                              onsubmit="return confirm('¿Seguro que deseas eliminar esta pieza del catálogo?');">
                            <input type="hidden" name="id" value="${p.id}">
                            <button type="submit" class="btn-delete-prod">Eliminar</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</section>

<script>
    var f = document.getElementById('flash-msg');
    if (f) { setTimeout(function () { f.style.display = 'none'; }, 5000); }
</script>
</body>
</html>
