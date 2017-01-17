//package ru.holyway;
//
//import org.telegram.telegrambots.TelegramBotsApi;
//import org.telegram.telegrambots.exceptions.TelegramApiException;
//
//public class DemoApplication {
//    public static TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
//
//    public static void main(String[] args) {
//        //SpringApplication.run(DemoApplication.class, args);
//        try {
//            telegramBotsApi.registerBot(new BotInitializer());
//
//        } catch (TelegramApiException e) {
//            System.out.println("aaaaaaaaaaa " + e);
//        }
//    }
//
////	@Autowired
////	SimpleTalkBot botInitializer;
////
////	@Bean
////	SimpleTalkBot getBotInitializer(){
////		return new SimpleTalkBot(new ConcurrentTaskScheduler());
////	}
////
////	@Autowired
////	BotInitializer botInitializer2;
////
////	@Bean
////	BotInitializer getBotInitializer2(){
////		return new BotInitializer();
////	}
//}
