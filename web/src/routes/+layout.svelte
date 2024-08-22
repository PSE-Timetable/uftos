<script>
  import { Toaster } from 'svelte-sonner';
  import '../app.css';
  import { page } from '$app/stores';
</script>

<svelte:head>
  <title>{$page.data.meta.title}</title>
</svelte:head>

<slot />
<Toaster class="text-primary" />
