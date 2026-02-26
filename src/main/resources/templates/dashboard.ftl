<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Legend Versions Dashboard</title>
    <link rel="stylesheet" href="assets/style.css">
    <script src="assets/htmx.min.js"></script>
</head>
<body>
    <header>
        <h1>Legend Versions Dashboard</h1>
        <p class="subtitle">${data.primaryDisplayName()} release history with dependency versions</p>
    </header>

    <main>
        <#list data.releases() as release>
        <div class="release-card">
            <div class="release-header" onclick="this.parentElement.classList.toggle('expanded')">
                <h2>
                    <span class="version-badge">${data.primaryDisplayName()} ${release.version}</span>
                    <span class="tag-name">${release.tag}</span>
                </h2>
                <span class="expand-icon">&#9660;</span>
            </div>

            <div class="version-table-wrap">
                <table class="version-table">
                    <thead>
                        <tr>
                            <th>Dependency</th>
                            <th>Version</th>
                            <th>Changed</th>
                        </tr>
                    </thead>
                    <tbody>
                        <#list release.dependencies as dep>
                        <tr class="${dep.changed()?then('changed', '')}">
                            <td>${dep.displayName()}</td>
                            <td><code>${dep.version()}</code></td>
                            <td>
                                <#if dep.changed()>
                                    <span class="change-indicator" title="was ${dep.previousVersion()}">&#x2191; from ${dep.previousVersion()}</span>
                                <#elseif release.hasPreviousRelease()>
                                    <span class="no-change">&mdash;</span>
                                </#if>
                            </td>
                        </tr>
                        </#list>
                    </tbody>
                </table>
            </div>

            <#if release.hasPreviousRelease()>
            <div class="commits-section">
                <h3>Commits in ${data.primaryDisplayName()} ${release.previousTag} &rarr; ${release.tag}</h3>
                <div hx-get="commits/${data.primaryKey()}?from=${release.previousTag}&amp;to=${release.tag}"
                     hx-trigger="intersect once"
                     hx-swap="innerHTML"
                     class="commits-container">
                    <p class="loading">Loading ${data.primaryDisplayName()} commits...</p>
                </div>

                <#list release.changedDependencies as dep>
                <h3>Commits in ${dep.displayName()} ${dep.previousVersion()} &rarr; ${dep.version()}</h3>
                <div hx-get="commits/${dep.key()}?from=${dep.previousVersion()}&amp;to=${dep.version()}"
                     hx-trigger="intersect once"
                     hx-swap="innerHTML"
                     class="commits-container">
                    <p class="loading">Loading ${dep.displayName()} commits...</p>
                </div>
                </#list>
            </div>
            </#if>
        </div>
        </#list>
    </main>
</body>
</html>
