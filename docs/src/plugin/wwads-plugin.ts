module.exports = function myPlugin(context, options) {
    return {
        name: 'wwads-plugin',
        injectHtmlTags() {
            return {
                // preBodyTags: [`<div class="wwads-cn wwads-vertical wwads-sticky" data-id="312" style="max-width:180px;position:fixed;bottom:0;right:0"></div>`],
                preBodyTags: [``],
                postBodyTags: [`
<script>

function handleUrlChange(){
      const targetDiv = document.querySelector('div.col.col--3');
      const wwadsDiv = document.querySelector('div.col.col--3').querySelector("div.wwads-cn");
      if (targetDiv&&!wwadsDiv) {
          targetDiv.insertAdjacentHTML('beforeend', '<div class="wwads-cn wwads-horizontal" data-id="312" style="min-height: 0px; border: 1px #eee solid; margin-bottom: 12px; z-index: 1; position: fixed"></div>');
      }
}
const observer = new MutationObserver(() => {
   try {
      handleUrlChange()
   }catch (e) {
     
   }
});
observer.observe(document, { subtree: true, childList: true });
</script>
`],


            };
        },

    };
}
