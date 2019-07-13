<#import "parts/common.ftl" as c>
<@c.page>

<link rel="stylesheet" href="../static/css/style.css">
    <#if message! == "error">
<div class="alert alert-danger" role="alert">
    Такое имя пользователя уже зарегестрировано!
</div>
    </#if>
<h5 class="mt-3 mb-3 text-center featurette-heading">Регистрация</h5>
<div class="container mt-5 mb-3">
    <form action="/registration" method="post">
        <div class="form-row">
            <div class="col-md-4 mb-3">
                <label for="validationUsername">Имя пользователя</label>
                <input type="text" class="form-control ${(usernameError??)?string('is-invalid','')}" id="validationUsername" name="username" placeholder="Имя пользователя"
                       value="<#if user??>${user.username}</#if>" required>
                <#if usernameError??>
                    <div class="invalid-feedback">
                        ${usernameError}
                    </div>
                </#if>
            </div>
            <div class="col-md-4 mb-3">
                <label for="validationPassword">Пароль</label>
                <input type="password" class="form-control ${(passwordError??)?string('is-invalid','')}" id="validationPassword" name="password" placeholder="Пароль" required>
                <#if passwordError??>
                    <div class="invalid-feedback">
                        ${passwordError}
                    </div>
                </#if>
            </div>
            <div class="col-md-4 mb-3">
                <label for="inputRole">Роль</label>
                <select class="custom-select" id="inputRole" name="role" required>
                    <#list roles as roles>
                        <option value="${roles}">${roles}</option>
                    </#list>
                </select>
            </div>
        </div>

        <div class="container text-center">
            <button class="btn btn-primary" type="submit">Зарегистрироваться</button>
        </div>
        <input type="hidden" name="_csrf" value="${_csrf.token}"/>
    </form>
</div>
    <#include "parts/footer.ftl">
</@c.page>