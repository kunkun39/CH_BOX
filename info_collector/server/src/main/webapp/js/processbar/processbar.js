$(document).ready(function(){
    jQuery.fn.anim_progressbar = function (aOptions) {
        // def values
        var iCms = 1000;
        var iMms = 60 * iCms;
        var iHms = 3600 * iCms;
        var iDms = 24 * 3600 * iCms;

        // def options
        var aDefOpts = {
            start: new Date(), // now
            finish: new Date().setTime(new Date().getTime() + 60 * iCms), // now + 60 sec
            interval: 5000
        }

        var aOpts = jQuery.extend(aDefOpts, aOptions);
        var vPb = this;

        // each progress bar
        return this.each(
            function() {
                var iDuration = aOpts.finish - aOpts.start;

                // calling original progressbar
                $(vPb).children('.pbar').progressbar();

                // looping process
                var vInterval = setInterval(
                    function(){
                        $.ajax({
                            type: "post",
                            url: '/update/backend/fileuploadpercentage.html',
                            success:function(result){
                                var iPerc = result.percentage;

                                // display current positions and progress
                                $(vPb).children('.percent').html('<b>'+iPerc.toFixed(1)+'%</b>');
                                $(vPb).children('.pbar').children('.ui-progressbar-value').css('width', iPerc+'%');

                                // in case of Finish
                                if (iPerc >= 100) {
                                    clearInterval(vInterval);
                                    $(vPb).children('.percent').html('<b>100%</b>');
                                }
                            }
                        });

                    } , aOpts.interval
                );
            }
        );
    }

});