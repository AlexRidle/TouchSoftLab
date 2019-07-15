<#import "parts/common.ftl" as c>

<@c.page>
    <link rel="stylesheet" href="/static/css/error-template.css">
    <div class="container">
        <div class="starter-template">
            <h1 class="mb-5">Произошла ошибка</h1>
            <p class="lead mt-3">Ссылка запрашиваемую вами страницу может быть нерабочей или была удалена.</p>
            <p class="lead">Если вы думаете, что это ошибка, убедитесь в наличии </p>
            <p class="lead">необходимого уровня доступа вашей учетной записи.</p>
        </div>
    </div>
    <#include "parts/footer.ftl">

</@c.page>