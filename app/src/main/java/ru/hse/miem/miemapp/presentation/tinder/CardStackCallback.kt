package ru.hse.miem.miemapp.presentation.tinder

import android.view.View
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.Direction


class CardStackCallback: CardStackListener {

    var pos: Int = 0
    private var sorting = Sorting()


    override fun onCardDragging(direction: Direction, ratio: Float) {}

    override fun onCardSwiped(direction: Direction) {
        if (direction == Direction.Right && pos != 0){
            Sorting.likeIndexes.add(pos)
        }
        sorting.plus()
    }

    override fun onCardRewound() {}

    override fun onCardCanceled() {}

    override fun onCardAppeared(view: View, position: Int) {
        pos = position
    }

    override fun onCardDisappeared(view: View, position: Int) {}
}