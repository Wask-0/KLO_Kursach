# Конспекты — мобильное приложение для ведения конспектов

Курсовой проект по МДК 01.03 «Разработка мобильных приложений».
Kotlin, Android Views + ViewBinding, Material 3, Navigation Component, minSdk 24.

## Этап: ресурсы, макеты интерфейса, навигация, темы

### Активности
| Активность | Назначение |
|---|---|
| `ui/MainActivity` | Главная (single-activity): Toolbar, `NavHostFragment` (`nav_main`), нижняя навигация |
| `ui/auth/AuthActivity` | Вход и регистрация (`nav_auth`) |

### Фрагменты (экраны)
| Фрагмент | Макет | Назначение |
|---|---|---|
| `LoginFragment`, `RegisterFragment` | `fragment_login`, `fragment_register` | Вход / регистрация (ФИО, группа, email) |
| `NotesFragment` | `fragment_notes`, `item_note` | Все конспекты, фильтр по предметам, сортировка |
| `SubjectsFragment` | `fragment_subjects`, `item_subject` | Предметы (папки) |
| `SubjectNotesFragment` | `fragment_subject_notes` | Конспекты выбранного предмета |
| `SearchFragment` | `fragment_search` | Полнотекстовый поиск и поиск по тегам |
| `ProfileFragment` | `fragment_profile` | Профиль и статистика |
| `NoteEditorFragment` | `fragment_note_editor` | Создание / редактирование, панель форматирования |
| `OcrFragment` | `fragment_ocr` | Распознавание текста с фото (ML Kit — следующий этап) |
| `SummaryFragment` | `fragment_summary` | Краткое содержание с помощью ИИ |
| `ExportBottomSheet` | `bottom_sheet_export` | Экспорт в PDF / TXT, «Поделиться» |
| `SettingsFragment` | `xml/preferences.xml` | Настройки (тема, шрифт, офлайн, OCR, ИИ) |
| `AboutFragment` | `fragment_about` | О приложении |

### Навигация
- `res/navigation/nav_main.xml` — граф главной активности (переходы с анимациями, аргументы, диалог экспорта, глобальные действия).
- `res/navigation/nav_auth.xml` — граф авторизации.
- Нижняя навигация связана с графом через `setupWithNavController`, кнопка «Назад» в Toolbar — через `AppBarConfiguration`.

### Меню (`res/menu`)
`bottom_nav_menu` (нижняя навигация), `menu_main` (тема / настройки / о приложении),
`menu_notes` (сортировка, подменю), `menu_note_editor` (сохранить, OCR, ИИ, экспорт, удалить),
`menu_note_item` (всплывающее меню карточки), `menu_subjects` (добавить предмет).

### Ресурсы
- `values/strings.xml` — все строки интерфейса на русском; `arrays.xml` — списки для настроек и цвета предметов.
- `values/colors.xml` и `values-night/colors.xml` — палитры светлой и тёмной темы (Material 3).
- `values/themes.xml` и `values-night/themes.xml` — тема `Theme.KLO_Kursach` (DayNight).
- `values/styles.xml` — типографика, формы, стили карточек, кнопок, полей ввода.
- `values/dimens.xml`, `values/integers.xml`, `values-w600dp/integers.xml` — размеры и адаптация под планшеты.
- `font/` — шрифт Roboto (Regular, Italic, Medium, Bold) и семейство `roboto.xml`.
- `drawable/` — векторные иконки, логотип, иллюстрации, фоны; `color/` — селектор цвета навигации; `anim/` — анимации переходов.

### Тёмная и светлая тема
Тема выбирается в «Настройках» (системная / светлая / тёмная) или быстрым переключателем в меню Toolbar.
Логика — `util/ThemeManager` (`AppCompatDelegate.setDefaultNightMode`), применяется при старте в `KloApp`.

### Данные
Пока данные хранятся в памяти (`data/NotesRepository`, заполнен примерами), профиль — в SharedPreferences
(`data/UserSession`). На следующем этапе хранилище переводится на Room.
