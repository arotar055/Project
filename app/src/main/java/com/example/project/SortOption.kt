package com.example.project

enum class SortOption(val title: String) {
    TIME_ASC("По времени (ближайшие)"),
    TIME_DESC("По времени (поздние)"),
    CREATED_DESC("Сначала новые"),
    CREATED_ASC("Сначала старые"),
    TITLE_ASC("По названию (A–Z)"),
    TITLE_DESC("По названию (Z–A)")
}

fun todoComparator(option: SortOption): Comparator<TodoItem> {
    return when (option) {
        SortOption.TIME_ASC ->
            Comparator { a, b ->
                when {
                    a.remindAt == null && b.remindAt == null -> 0
                    a.remindAt == null -> 1
                    b.remindAt == null -> -1
                    else -> a.remindAt.compareTo(b.remindAt)
                }
            }

        SortOption.TIME_DESC ->
            Comparator { a, b ->
                when {
                    a.remindAt == null && b.remindAt == null -> 0
                    a.remindAt == null -> 1
                    b.remindAt == null -> -1
                    else -> b.remindAt.compareTo(a.remindAt)
                }
            }

        SortOption.CREATED_DESC -> Comparator { a, b -> b.id.compareTo(a.id) }
        SortOption.CREATED_ASC -> Comparator { a, b -> a.id.compareTo(b.id) }

        SortOption.TITLE_ASC -> Comparator { a, b -> a.title.lowercase().compareTo(b.title.lowercase()) }
        SortOption.TITLE_DESC -> Comparator { a, b -> b.title.lowercase().compareTo(a.title.lowercase()) }
    }
}
