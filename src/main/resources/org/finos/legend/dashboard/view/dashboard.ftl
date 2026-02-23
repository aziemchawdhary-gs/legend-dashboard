<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Legend Versions Dashboard</title>
    <link rel="stylesheet" href="/assets/style.css">
    <script src="/assets/htmx.min.js"></script>
</head>
<body>
    <header>
        <h1>Legend Versions Dashboard</h1>
        <p class="subtitle">SDLC release history with dependency versions</p>
    </header>

    <main>
        <#list data.releases() as release>
        <div class="release-card">
            <div class="release-header" onclick="this.parentElement.classList.toggle('expanded')">
                <h2>
                    <span class="version-badge">SDLC ${release.version}</span>
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
                        <tr class="${release.engineChanged()?then('changed', '')}">
                            <td>legend-engine</td>
                            <td><code>${release.deps.engineVersion()}</code></td>
                            <td>
                                <#if release.engineChanged()>
                                    <span class="change-indicator" title="was ${release.previousDeps.engineVersion()}">&#x2191; from ${release.previousDeps.engineVersion()}</span>
                                <#elseif release.hasPreviousRelease()>
                                    <span class="no-change">&mdash;</span>
                                </#if>
                            </td>
                        </tr>
                        <tr class="${release.pureChanged()?then('changed', '')}">
                            <td>legend-pure</td>
                            <td><code>${release.deps.pureVersion()}</code></td>
                            <td>
                                <#if release.pureChanged()>
                                    <span class="change-indicator" title="was ${release.previousDeps.pureVersion()}">&#x2191; from ${release.previousDeps.pureVersion()}</span>
                                <#elseif release.hasPreviousRelease()>
                                    <span class="no-change">&mdash;</span>
                                </#if>
                            </td>
                        </tr>
                        <tr class="${release.sharedChanged()?then('changed', '')}">
                            <td>legend-shared</td>
                            <td><code>${release.deps.sharedVersion()}</code></td>
                            <td>
                                <#if release.sharedChanged()>
                                    <span class="change-indicator" title="was ${release.previousDeps.sharedVersion()}">&#x2191; from ${release.previousDeps.sharedVersion()}</span>
                                <#elseif release.hasPreviousRelease()>
                                    <span class="no-change">&mdash;</span>
                                </#if>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>

            <#if release.hasPreviousRelease()>
            <div class="commits-section">
                <h3>Commits in SDLC ${release.previousTag} &rarr; ${release.tag}</h3>
                <div hx-get="/commits/sdlc?from=${release.previousTag}&amp;to=${release.tag}"
                     hx-trigger="intersect once"
                     hx-swap="innerHTML"
                     class="commits-container">
                    <p class="loading">Loading SDLC commits...</p>
                </div>

                <#if release.engineChanged()>
                <h3>Commits in engine ${release.previousDeps.engineVersion()} &rarr; ${release.deps.engineVersion()}</h3>
                <div hx-get="/commits/engine?from=${release.previousDeps.engineVersion()}&amp;to=${release.deps.engineVersion()}"
                     hx-trigger="intersect once"
                     hx-swap="innerHTML"
                     class="commits-container">
                    <p class="loading">Loading engine commits...</p>
                </div>
                </#if>

                <#if release.pureChanged()>
                <h3>Commits in pure ${release.previousDeps.pureVersion()} &rarr; ${release.deps.pureVersion()}</h3>
                <div hx-get="/commits/pure?from=${release.previousDeps.pureVersion()}&amp;to=${release.deps.pureVersion()}"
                     hx-trigger="intersect once"
                     hx-swap="innerHTML"
                     class="commits-container">
                    <p class="loading">Loading pure commits...</p>
                </div>
                </#if>

                <#if release.sharedChanged()>
                <h3>Commits in shared ${release.previousDeps.sharedVersion()} &rarr; ${release.deps.sharedVersion()}</h3>
                <div hx-get="/commits/shared?from=${release.previousDeps.sharedVersion()}&amp;to=${release.deps.sharedVersion()}"
                     hx-trigger="intersect once"
                     hx-swap="innerHTML"
                     class="commits-container">
                    <p class="loading">Loading shared commits...</p>
                </div>
                </#if>
            </div>
            </#if>
        </div>
        </#list>
    </main>
</body>
</html>
