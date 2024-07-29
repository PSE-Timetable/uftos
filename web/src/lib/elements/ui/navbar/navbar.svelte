<script lang="ts">
	import Button from './../button/button.svelte';
	import { goto } from '$app/navigation';
	import { ChevronLeft } from 'lucide-svelte/icons';
  let title: string = 'Title';
  export { title };
</script>

<div class="h-[10%] bg-primary text-white p-4 font-medium text-2xl">
  <Button
    on:click={async () => {
      await goto('../');
    }}
    variant="secondary"
    size="icon"
    class="rounded-full bg-accent mr-6"
  >
    <ChevronLeft class="h-5 w-5 text-white" />
  </Button>
  {title}
  <slot />
</div>
