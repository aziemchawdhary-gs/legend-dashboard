package org.finos.legend.dashboard.service;

import org.junit.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class PomParserTest {

    private final PomParser parser = new PomParser();

    @Test
    public void extractsLegendProperties() {
        String pom = """
                <?xml version="1.0" encoding="UTF-8"?>
                <project>
                    <modelVersion>4.0.0</modelVersion>
                    <groupId>org.finos.legend</groupId>
                    <artifactId>legend-sdlc</artifactId>
                    <version>0.180.0</version>
                    <properties>
                        <legend.engine.version>4.62.1</legend.engine.version>
                        <legend.pure.version>4.10.1</legend.pure.version>
                        <legend.shared.version>10.28.0</legend.shared.version>
                        <maven.compiler.source>17</maven.compiler.source>
                    </properties>
                </project>
                """;

        Map<String, String> props = parser.extractProperties(pom);

        assertThat(props).containsEntry("legend.engine.version", "4.62.1");
        assertThat(props).containsEntry("legend.pure.version", "4.10.1");
        assertThat(props).containsEntry("legend.shared.version", "10.28.0");
        assertThat(props).containsEntry("maven.compiler.source", "17");
    }

    @Test
    public void handlesNoProperties() {
        String pom = """
                <?xml version="1.0" encoding="UTF-8"?>
                <project>
                    <modelVersion>4.0.0</modelVersion>
                </project>
                """;

        Map<String, String> props = parser.extractProperties(pom);

        assertThat(props).isEmpty();
    }
}
