<script lang="ts">
  import { page } from '$app/stores';

  export let text: string;
  export let url: string;
  let active: boolean = false;

  $: active = $page.url.pathname === url;
</script>

<a
  href={url}
  class="{active
    ? 'bg-white text-primary'
    : 'text-white'}  text-base px-6 py-2 hover:bg-accent hover:text-white hover:text rounded-full font-normal"
>
  {text}
</a>
