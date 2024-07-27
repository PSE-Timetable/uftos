<script lang="ts">
  import ChevronLeft from 'lucide-svelte/icons/chevron-left';
  import { Button } from '$lib/elements/ui/button/index.js';
  import { page } from '$app/stores';
  import { goto } from '$app/navigation';
</script>

<div class="flex flex-row justify-start bg-foreground md:p-4 text-white">
  <Button
    on:click={async () => {
      await goto('./');
    }}
    variant="secondary"
    size="icon"
    class="rounded-full bg-accent mr-6"
  >
    <ChevronLeft class="h-5 w-5 text-white" />
  </Button>
  <h1 class="font-bold text-xl mt-1">{$page.data['meta']['title']}</h1>
</div>

<slot />
