# Космос

Мобильный справочник по космической отрасли: предстоящие запуски ракет, значимые
события, экспедиции, космические агентства и астронавты.

## Возможности

- Нижняя навигация с двумя вкладками: **Главная** и **Настройки** (заглушка).
- На главной - пять категорий: запуски, события, экспедиции, агентства, астронавты.
- Списки элементов на `LazyColumn` - виртуализированное отображение для
  большого количества записей.
- Экран деталей по каждому элементу: разметка на `ConstraintLayout`,
  статистика, описание и справочные поля.
- Единая обработка состояний экрана: загрузка / ошибка с повтором / данные.

## Стек

- Kotlin 2.2, Android Compose (Jetpack Compose), Material 3, dynamic color
- Навигация: `navigation-compose` (NavHost, стеки списков с аргументами-идентификаторами)
- DI: Hilt; ViewModel из `androidx.lifecycle` (паттерн MVVM)
- Сериализация DTO-моделей: kotlinx.serialization; корутины (`kotlinx-coroutines`)
- Загрузка изображений по URL: Coil (`coil-compose`)
- Сборка: Gradle + Android Gradle Plugin 9

## Архитектура

MVVM: экраны (`ui/*`) не обращаются к данным напрямую - они получают состояние
из `@HiltViewModel`, которые ходят в `SpaceRepository`. Сейчас репозиторий отдаёт
мок-данные (`MockSpaceData`).

```
app/src/main/java/com/example/android_development_practices/
├── App.kt                  # @HiltAndroidApp
├── MainActivity.kt         # Single Activity: тема → поверхность → MainApp
├── data/
│   ├── model/dto/          # DTO, повторяющие схему ответов API
│   └── repository/         # SpaceRepository + мок-реализация
├── di/                     # Hilt-модуль и квалификаторы
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