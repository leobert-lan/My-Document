var smoothScrollToElement = function (element) {
    var topOffset = getTopOffset(element);
    window.javaScrollImpl.smoothScrollToY(topOffset);
};

function getTopOffset(element) {
    console.log("id:"+element.id);
    var oPos = getTop(element);
    return oPos;
};

function getTop(e){
    var offset=e.offsetTop;
    if(e.offsetParent!=null)
    offset+=getTop(e.offsetParent);
    console.log("offset:"+offset);
    return offset;
};