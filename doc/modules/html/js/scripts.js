$(document).ready(function() {

    var sections = [];
    var nav = $('body').children('nav');
    var links = nav.find('a');

    links.each(function() {
        var link = $(this);
        sections.push({
            nav: link,
            main: $(link.attr('href'))
        });
    });

    $('main').on('scroll resize', function() {
        var i = 0;
        while (i < sections.length) {
            var offset = sections[i].main.offset().top;
            if (offset >= 0)
                break;
            ++i;
        }
        links.removeClass('current');
        sections[i].nav.addClass('current');
    }).trigger('scroll');

    $('#menu-switcher').on('click', function() {
        if (nav.hasClass('showed'))
            nav.removeClass('showed');
        else
            nav.addClass('showed');
    });

    links.on('click', function() {
        nav.removeClass('showed');
    });

});