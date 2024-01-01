package dev.foxikle.proxyrestarter;

import com.google.inject.Inject;
import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.slf4j.Logger;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Plugin(
        id = "proxyrestarter",
        name = "ProxyRestarter",
        version = BuildConstants.VERSION
)
public class ProxyRestarter {

    @Inject
    private Logger logger;

    @Inject
    private ProxyServer server;

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        server.getCommandManager().register(server.getCommandManager().metaBuilder("restartproxy").build(), new RestartCommand(server, this));
    }

    private class RestartCommand implements SimpleCommand {

        private final ProxyServer proxy;
        private final ProxyRestarter plugin;

        private RestartCommand(ProxyServer proxy, ProxyRestarter plugin) {
            this.proxy = proxy;
            this.plugin = plugin;
        }

        @Override
        public void execute(Invocation invocation) {
            proxy.getAllPlayers().forEach(player -> player.sendMessage(Component.text("WARNING!", NamedTextColor.RED, TextDecoration.BOLD).append(Component.text(" This proxy is restarting in 30 seconds.", NamedTextColor.AQUA).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))));
            proxy.getScheduler().buildTask(plugin, () -> {
                proxy.getAllPlayers().forEach(player -> player.disconnect(Component.text("XXX", NamedTextColor.RED, TextDecoration.OBFUSCATED).append(Component.text("This proxy is restarting.", NamedTextColor.RED).decoration(TextDecoration.OBFUSCATED, TextDecoration.State.FALSE)).append(Component.text("XXX", NamedTextColor.RED, TextDecoration.OBFUSCATED))));
                proxy.shutdown();
            }).delay(Duration.of(30, ChronoUnit.SECONDS)).schedule();
        }

        @Override
        public List<String> suggest(Invocation invocation) {
            return SimpleCommand.super.suggest(invocation);
        }

        @Override
        public CompletableFuture<List<String>> suggestAsync(Invocation invocation) {
            return SimpleCommand.super.suggestAsync(invocation);
        }

        @Override
        public boolean hasPermission(Invocation invocation) {
            return !(invocation.source() instanceof Player);
        }
    }
}
