<#if commits?size == 0>
    <p class="empty">No commits found in this range.</p>
<#else>
<table class="commits-table">
    <thead>
        <tr>
            <th>Hash</th>
            <th>Message</th>
        </tr>
    </thead>
    <tbody>
        <#list commits as commit>
        <tr>
            <td><code>${commit.shortHash()}</code></td>
            <td>${commit.message()}</td>
        </tr>
        </#list>
    </tbody>
</table>
<p class="commit-count">${commits?size} commit<#if commits?size != 1>s</#if></p>
</#if>
