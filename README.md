# Космос

Мобильный справочник по космической отрасли: предстоящие запуски ракет, значимые
события, экспедиции, космические агентства и астронавты.

## Возможности

- На главной - пять категорий: запуски, события, экспедиции, агентства, астронавты.
- Списки элементов на `LazyColumn` - виртуализированное отображение для
  большого количества записей.
- Экран деталей по каждому элементу: разметка на `ConstraintLayout`,
  статистика, описание и справочные поля.
- Загрузка актуальных данных Launch Library 2: состояния загрузки, ошибки и повтора.
- Детальные страницы загружаются отдельными запросами по ID.

## Стек

- Kotlin 2.2, Android Compose (Jetpack Compose), Material 3, dynamic color
- Навигация: `navigation-compose` (NavHost, стеки списков с аргументами-идентификаторами)
- DI: Hilt; ViewModel из `androidx.lifecycle` (паттерн MVVM)
- Сеть: Retrofit с suspend API и kotlinx.serialization; корутины (`kotlinx-coroutines`)
- Загрузка изображений по URL: Coil (`coil-compose`)
- Сборка: Gradle + Android Gradle Plugin 9

## Архитектура

MVVM с разделением на представление, домен и данные: экраны наблюдают состояние
`@HiltViewModel`; ViewModel вызывают `SpaceUseCases`. Домен содержит интерфейс
`SpaceRepository` и модели приложения. В data `SpaceRepositoryImpl` запрашивает
ответы через Retrofit `SpaceApi` и преобразует DTO в доменные модели. Неизвестные
поля API игнорируются. Сетевые вызовы выполняются suspend-функциями на
`Dispatchers.IO`. Ошибки сети и HTTP (включая 404 и 429) отображаются с возможностью
повтора.

Базовый адрес API задаётся Gradle-параметром `spaceApiBaseUrl`. По умолчанию
используется сервер для разработки без лимитов (`https://lldev.thespacedevs.com/`).
Чтобы собрать приложение с основным сервером, передайте:

```bash
./gradlew assembleDebug -PspaceApiBaseUrl=https://ll.thespacedevs.com/
```

Основной сервер ограничивает неаутентифицированные запросы.

```
app/src/main/java/com/example/android_development_practices/
├── App.kt                  # @HiltAndroidApp
├── MainActivity.kt         # Single Activity: тема → поверхность → MainApp
├── data/
│   ├── model/dto/          # DTO ответов API
│   ├── mapper/             # преобразование DTO в доменные модели
│   ├── remote/             # Retrofit-интерфейс Launch Library API
│   └── repository/         # API-реализация доменного репозитория
├── domain/
│   ├── model/              # модели приложения
│   ├── repository/         # интерфейс репозитория
│   └── SpaceUseCases.kt    # сценарии получения данных для UI
├── di/                     # Retrofit, Hilt и квалификаторы
└── ui/
    ├── navigation/         # нижняя навигация и маршруты NavHost
    ├── common/             # каркасы экранов, карточки списка, UiState, форматтеры
    ├── home/               # главный экран с категориями
    └── launch|event|expedition|agency|astronaut/
                            # по паре экранов: список + детали (View + ViewModel)
```

## Сборка и запуск

Требуется Android SDK (путь задаётся в `local.properties`).

```bash
./gradlew assemble        # сборка APK (debug в app/build/outputs/apk/debug)
./gradlew test            # тесты
```

Либо откройте проект в Android Studio и запустите конфигурацию `app`.

## API

Используемая документация схемы API находится в `app/develop_data/api.json`.
Реализованы списки и детали для запусков (`launches/upcoming`), предстоящих
событий (`events/upcoming`), экспедиций, агентств и астронавтов. Для списков
задаются query-параметры `limit` и `mode`; для агентств используется постраничная
загрузка с `limit`, `offset` и сортировкой `ordering=name`. Для деталей передаётся
ID в пути.
