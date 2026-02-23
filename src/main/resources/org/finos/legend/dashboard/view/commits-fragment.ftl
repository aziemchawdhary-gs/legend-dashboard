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
            <td><#if githubUrl??><a href="${githubUrl}/commit/${commit.hash()}" class="commit-link"><code>${commit.shortHash()}</code></a><#else><code>${commit.shortHash()}</code></#if></td>
            <td>${view.linkifyMessage(commit.message())?no_esc}</td>
        </tr>
        </#list>
    </tbody>
</table>
<p class="commit-count">${commits?size} commit<#if commits?size != 1>s</#if></p>
</#if>
