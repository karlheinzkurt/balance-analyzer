$(document).ready(function()
{
    $("div.transactionIntervalHeader.month a.toggleButton").click(function(e)
    {
        console.log("Month toggled");
        $( this ).parent().siblings("table.transactions.month").each(function()
        {
            $( this ).fadeToggle()
        });
    });
    $("div.transactionIntervalHeader.year a.toggleButton").click(function(e)
    {
        console.log("Year toggled");
        $( this ).parent().siblings("div.monthTransactionInterval").each(function()
        {
            $( this ).fadeToggle()
        });
    });
    $("#collapseMonths").click(function()
    {
        $("table.transactions.month").each(function()
        {
            $( this ).fadeToggle()
        });
    });
    $("#collapseYears").click(function()
    {
        $("div.monthTransactionInterval").each(function()
        {
            $( this ).fadeToggle()
        });
    });
});
