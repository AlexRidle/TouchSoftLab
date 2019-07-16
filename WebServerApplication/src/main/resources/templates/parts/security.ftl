<#assign
known = Session.SPRING_SECURITY_CONTEXT??
>

<#if known>
    <#assign
    user = Session.SPRING_SECURITY_CONTEXT.authentication.principal
    name = user.getUsername()
    role = user.getRole()
    >
<#else>
    <#assign
    name = "Guest"
    role = "Client"
    >
</#if>