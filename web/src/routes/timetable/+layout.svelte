<script>
  import Navbar from '$lib/elements/ui/navbar/navbar.svelte';
</script>

<div class="flex flex-col gap-2">
  <Navbar title="Stundenplan" />
  <div class="p-4">
    <slot />
  </div>
</div>
