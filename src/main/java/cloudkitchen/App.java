package src.main.java.cloudkitchen;

import cloudkitchen.Kitchen;
import cloudkitchen.constants.DeliverySimConstants;
import cloudkitchen.Courier;
import cloudkitchen.impl.*;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.AllArgsConstructor;
import model.Config;
import model.Order;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@AllArgsConstructor
public class App {

    private Gson gson;

    public Config parseConfigFile() {
        try (Reader reader = Files.newBufferedReader(Paths.get(DeliverySimConstants.CONFIG_FILE))) {
            Config config = gson.fromJson(reader, Config.class);
            System.out.println("Configuration: " + config);
            System.out.println("----------------------------------\n");
            return config;
        } catch (IOException e) {
            e.printStackTrace();
            // in case of error while reading configuration, we set some defaults
            return Config.builder()
                    .orderSleep(1000)
                    .ingestionLimit(10)
                    .ingestionRate(2)
                    .overflowCapacity(15)
                    .overflowDecayRate(2)
                    .build();
        }
    }

    public List<Order> parseOrderFile() {
        try (Reader reader = Files.newBufferedReader(Paths.get(DeliverySimConstants.ORDERS_FILE))) {
            List<Order> orderList = gson.fromJson(reader, new TypeToken<List<Order>>() {}.getType());
            return orderList;
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Starting Delivery Simulation");

        // Initiate all elements for the simulation
        // App will initiate this main application to parse configurations
        // The kitchen and courier will be called from this system
        final App app = new App(new Gson());
        final Config config = app.parseConfigFile();
        final List<Order> orders = app.parseOrderFile();
        final ExecutorService executorService = Executors.newFixedThreadPool(config.getNThreads());
        final Kitchen kitchen = new Kitchen(ImmutableList.of(
                new HotShelf(DeliverySimConstants.HOT_NAME),
                new ColdShelf(DeliverySimConstants.COLD_NAME),
                new FrozenShelf(DeliverySimConstants.FROZEN_NAME),
                new OverflowShelf(DeliverySimConstants.OVERFLOW_NAME, config.getOverflowCapacity(), config.getOverflowDecayRate())));
        final Courier courier = new UberEats(kitchen, executorService, new ConcurrentLinkedDeque<>());

        if (config.getIngestionLimit() > orders.size()) {
            config.setIngestionLimit(orders.size());
        }

        for(int i = 0; i < config.getIngestionLimit(); i += config.getIngestionRate()) {
            List<Order> request = new ArrayList();
            for (int j = 0; j < config.getIngestionRate(); j++) {
                System.out.println("Received order: " + orders.get(i + j));
                request.add(orders.get(i + j));
            }

            kitchen.prepareOrders(ImmutableList.<Order>builder()
                    .addAll(request)
                    .build());
            courier.dispatch(ImmutableList.<Order>builder()
                    .addAll(request)
                    .build());

            Thread.sleep(config.getOrderSleep());
        }

        executorService.shutdown();
    }
}
