package tech.penser

import org.apache.camel.builder.RouteBuilder

class MyRoutes : RouteBuilder() {
    override fun configure() {
        // This will ensure that the file specified below is always present with the text shown
        from("timer:myTimer?period=1000")
            .id("my-timer")
            .log("tick")
            .setBody()
            .simple("A simple string")
            .to("file:./output?fileName=test.txt")

        // This watches the listed directory for any changes to files and reports them on the console
        from("file-watch:./output?events=CREATE,MODIFY,DELETE")
            .id("file-watcher")
            .log("\${header.CamelFileName} : \${header.CamelFileEventType}")

        // This will listen to a Slack channel for new messages,
        // and with the right permissions for the Slack bot, it can also post
        from("slack://general?token=RAW({{env:SLACK_CAMEL_BOT_TOKEN}})")
            .id("slack-bot")
            .log("\${body}")
//            .convertBodyTo(com.slack.api.model.Message::class.java)
//        Need to figure out how to prevent the line below from triggering with its own messaging
//            .to("slack://general?iconEmoji=:camel:&token=RAW({{env:SLACK_CAMEL_BOT_TOKEN}})")
    }
}
