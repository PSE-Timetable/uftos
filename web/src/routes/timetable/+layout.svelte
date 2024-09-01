<script>
  import Navbar from '$lib/elements/ui/navbar/navbar.svelte';
</script>

<div class="flex flex-col gap-2 w-fit min-w-full">
  <Navbar title="Stundenplan" backToRoot={true} />
  <div class="px-4">
    <slot />
  </div>
</div>
