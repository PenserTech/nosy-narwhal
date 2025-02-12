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
            // escape the '$' to prevent string interpolation
            .log("\${header.CamelFileName} : \${header.CamelFileEventType}")

        // This will listen to a Slack channel for new messages,
        // and with the right permissions for the Slack bot, it can also post
        from("slack://general?token=RAW({{env:SLACK_CAMEL_BOT_TOKEN}})")
            .id("slack-bot")
            .process { exchange ->
                val payload = exchange.getMessage().getBody(com.slack.api.model.Message::class.java)
                exchange.message.setHeader("userId", payload.user)
            }
            // filter out messages coming from the bot
            .filter { exchange -> exchange.message.getHeader("userId") != "U078BNJH8BH" }
            .log("\${body}")
            .to("slack://general?token=RAW({{env:SLACK_CAMEL_BOT_TOKEN}})")
    }
}
