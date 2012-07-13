function main()
{
   // Widget instantiation metadata...
   model.widgets = [];
   var rulesList = {
      name : "Alfresco.RulesList",
      options : {
         siteId : (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "",
         nodeRef : (page.url.args.nodeRef != null) ? page.url.args.nodeRef : "",
         filter : (args.filter != null) ? args.filter : "",
         selectDefault : (args.selectDefault != null) ? args.selectDefault : "false",
         editable : (args.editable != null) ? args.editable : "false"
      }
   };
   model.widgets.push(rulesList);
}

main();

