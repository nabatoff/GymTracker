# GymTracker - Android-приложение для отслеживания тренировок

## Описание
GymTracker - полнофункциональное Android-приложение для отслеживания тренировок, программ упражнений, прогресса и истории тренировок.

## Технологический стек
- **Язык**: Kotlin
- **UI**: Jetpack Compose
- **Архитектура**: MVVM (Model-View-ViewModel)
- **База данных**: Room (локальная база данных)
- **Навигация**: Jetpack Navigation for Compose

## Структура проекта

```
app/src/main/java/com/gymtracker/
├── data/
│   ├── entities/           # Room Entity классы
│   │   ├── WorkoutSession.kt
│   │   ├── BodyMetric.kt
│   │   ├── WorkoutProgram.kt
│   │   ├── Exercise.kt
│   │   └── Goal.kt
│   ├── dao/               # Data Access Objects
│   │   ├── WorkoutSessionDao.kt
│   │   ├── BodyMetricDao.kt
│   │   ├── WorkoutProgramDao.kt
│   │   ├── ExerciseDao.kt
│   │   └── GoalDao.kt
│   ├── database/
│   │   └── AppDatabase.kt
│   └── repository/
│       └── AppRepository.kt
├── ui/
│   ├── screens/           # Composable экраны
│   │   ├── HomeScreen.kt
│   │   ├── ProgramsScreen.kt
│   │   ├── ProgramDetailScreen.kt
│   │   ├── ProgressScreen.kt
│   │   └── HistoryScreen.kt
│   ├── viewmodels/        # ViewModels для экранов
│   │   ├── HomeViewModel.kt
│   │   ├── ProgramsViewModel.kt
│   │   ├── ProgressViewModel.kt
│   │   └── HistoryViewModel.kt
│   └── theme/             # Material Design тема
│       ├── Theme.kt
│       └── Type.kt
├── navigation/
│   └── NavGraph.kt        # Навигация приложения
├── MainActivity.kt
└── GymTrackerApp.kt       # Главный Composable приложения
```

## Модели данных

### 1. WorkoutSession
- `id`: Long (primary key, auto-generate)
- `startTime`: Long (timestamp начала тренировки)
- `endTime`: Long (timestamp окончания тренировки)
- `durationInMinutes`: Int (длительность в минутах)

### 2. BodyMetric
- `id`: Long (primary key, auto-generate)
- `date`: Long (timestamp)
- `weight`: Float (вес в кг)
- `bodyFatPercentage`: Float? (процент жира, опционально)
- `muscleMass`: Float? (мышечная масса в кг, опционально)

### 3. WorkoutProgram
- `programId`: Long (primary key, auto-generate)
- `name`: String (название программы)

### 4. Exercise
- `exerciseId`: Long (primary key, auto-generate)
- `programId`: Long (foreign key к WorkoutProgram)
- `name`: String (название упражнения)
- `sets`: Int (количество подходов)
- `reps`: String (повторения, например "8-12")

### 5. Goal
- `goalId`: Long (primary key, auto-generate)
- `title`: String (название цели)
- `targetValue`: Float (целевое значение)
- `currentValue`: Float (текущее значение)
- `unit`: String (единица измерения)

## Экраны приложения

### 1. Главная (HomeScreen)
- Большая кнопка "Начать тренировку" / "Закончить тренировку"
- Таймер, показывающий длительность текущей тренировки (ЧЧ:ММ:СС)
- При завершении тренировки автоматически сохраняет WorkoutSession в базу данных

### 2. Программы (ProgramsScreen)
- Список всех программ тренировок
- Кнопка "+" для добавления новой программы
- При клике на программу открывается ProgramDetailScreen
- Возможность удаления программ

### 3. Детали программы (ProgramDetailScreen)
- Список упражнений для выбранной программы
- Для каждого упражнения отображается: название, подходы, повторения
- Кнопка "+" для добавления нового упражнения
- Возможность удаления упражнений

### 4. Прогресс (ProgressScreen)
- **Форма ввода данных**: вес, % жира, мышечная масса
- **История веса**: последние 5 записей
- **Цели с Progress Bar**:
  - "Тренировок в месяц" (цель: 12)
  - "Целевой вес" (цель: 80 кг)
  - "Время в зале (в мес.)" (цель: 1000 минут)
- Автоматическое обновление прогресса целей

### 5. История (HistoryScreen)
- Список всех тренировок
- Для каждой тренировки отображается:
  - Дата и время начала
  - Длительность (часы и минуты)

## Функциональность

### Репозиторий (AppRepository)
- Инкапсулирует логику доступа к данным
- Все ViewModels работают через репозиторий
- Предоставляет методы для CRUD операций со всеми сущностями

### Навигация
- Bottom Navigation Bar с 4 основными экранами
- Навигация в детали программы тренировок
- Правильная обработка Back Stack

### База данных
- Предзаполнение базовых целей при первом запуске
- Каскадное удаление упражнений при удалении программы
- Flow для автоматического обновления UI

## Сборка и запуск

1. Откройте проект в Android Studio
2. Убедитесь, что у вас установлен Android SDK 34
3. Обновите путь к SDK в `local.properties` если необходимо
4. Синхронизируйте Gradle файлы
5. Запустите приложение на эмуляторе или реальном устройстве (API 26+)

## Зависимости

Все необходимые зависимости указаны в `app/build.gradle`:
- Jetpack Compose (BOM 2023.10.01)
- Room 2.6.0
- Navigation Compose 2.7.5
- Lifecycle Components 2.6.2
- Coroutines 1.7.3

## Примечания

- Минимальная версия Android: API 26 (Android 8.0)
- Целевая версия: API 34
- JVM Target: Java 17
- Kotlin версия: 1.9.0
