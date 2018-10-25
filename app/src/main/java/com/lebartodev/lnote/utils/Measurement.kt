package com.lebartodev.lnote.utils

import android.content.res.Resources
import android.util.TypedValue

fun Float.toPx(res: Resources): Float = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, res.displayMetrics)