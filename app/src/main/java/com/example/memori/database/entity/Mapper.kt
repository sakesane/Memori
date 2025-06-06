package com.example.memori.database.entity

import com.example.memori.algorithm.FSRSCard
import com.example.memori.algorithm.ReviewLog
import com.example.memori.algorithm.Status


// Card to FSRSCard
fun Card.toFSRSCard(): FSRSCard {
    val card = FSRSCard()
    card.due = this.due
    card.stability = this.stability
    card.difficulty = this.difficulty
    card.elapsedDays = this.elapsedDays
    card.scheduledDays = this.scheduledDays
    card.reps = this.reps
    card.lapses = this.lapses
    card.status = Status.from(this.status)
    card.lastReview = this.lastReview
    return card
}

// FSRSCard to Card
// 新建卡片时候如果传入cardId为0，数据库会自动生成一个新的ID
// 但我不知道更新卡片这样搞行不行，可能要写新的
fun FSRSCard.toCard(
    cardId: Long = 0,
    deckId: Long
): Card {
    return Card(
        cardId = cardId,
        deckId = deckId,
        due = this.due,
        stability = this.stability,
        difficulty = this.difficulty,
        elapsedDays = this.elapsedDays,
        scheduledDays = this.scheduledDays,
        reps = this.reps,
        lapses = this.lapses,
        status = this.status.value,
        lastReview = this.lastReview
    )
}

// ReviewLog to RevLog
fun ReviewLog.toRevLog(cardId: Long): Revlog {
    return Revlog(
        cardId = cardId,
        rating = this.rating.name,
        elapsedDays = this.elapsedDays,
        scheduledDays = this.scheduledDays,
        review = this.review,
        status = this.status.value
    )
}

