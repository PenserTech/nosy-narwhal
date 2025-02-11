package tech.penser

import org.apache.camel.builder.RouteBuilder

class MyRoutes : RouteBuilder() {
    override fun configure() {
        // This will ensure that the file specified below is always present with the text shown
        from("timer:myTimer?period=1000")
            .id("myTimer")
            .log("tick")
            .setBody()
            .simple("A simple string")
            .to("file:./output?fileName=test.txt")

        // This watches the listed directory for any changes to files and reports them on the console
        from("file-watch:./output?events=CREATE,MODIFY,DELETE")
            .id("file-watcher")
            .log("\${header.CamelFileName} : \${header.CamelFileEventType}")
    }
}
