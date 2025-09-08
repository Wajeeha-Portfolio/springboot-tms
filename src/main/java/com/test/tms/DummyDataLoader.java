package com.test.tms;

//@Component
public class DummyDataLoader {
//        implements CommandLineRunner {

//    private final TranslationRepo repository;
//
//    public DummyDataLoader(TranslationRepo repository) {
//        this.repository = repository;
//    }
//
//    @Override
//    public void run(String... args) {
//        List<String> locales = Arrays.asList("en", "fr", "es");
//        List<String> tags = Arrays.asList("mobile", "desktop", "web");
//
//        List<Translation> translations = new ArrayList<>();
//
//        IntStream.range(0, 100_000).forEach(i -> {
//            for (String locale : locales) {
//                Translation t = new Translation();
//                t.setField("key_" + i);
//                t.setLocale(locale);
//                t.setContent("Dummy content " + i + " in " + locale);
//                t.setTags(Collections.singletonList(tags.get(i % tags.size())));
//                translations.add(t);
//            }
//        });
//
//        repository.saveAll(translations);
//    }
}
