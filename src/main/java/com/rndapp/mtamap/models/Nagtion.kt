package com.rndapp.mtamap.models

/**
 * Created by ell on 6/9/17.
 */

class Nagtion {
    var defaultsKey = ""

    var title: String = ""
    var message: String = ""

    var yesText: String = ""
    var maybeText: String = ""
    var noText: String = ""

    var yesAction: (() -> Unit)? = {}
    var maybeAction: (() -> Unit)? = {}
    var noAction: (() -> Unit)? = {}
}
