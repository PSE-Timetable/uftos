<script lang="ts">
  import ChevronLeft from 'lucide-svelte/icons/chevron-left';
  import { Button } from '$lib/elements/ui/button/index.js';
  import pageName from './pageName';

  let heading: String;
  pageName.subscribe((text) => {
    heading = text;
  });
</script>

<div class="flex flex-row justify-start bg-foreground md:p-4 text-white -m-4">
  <Button
    on:click={() => {
      window.history.back(); //not the best solution to go back to previous page (href="../" doesnt seem to work)
    }}
    variant="secondary"
    size="icon"
    class="rounded-full bg-accent mr-6"
  >
    <ChevronLeft class="h-5 w-5 text-white" />
  </Button>
  <h1 class="font-bold text-xl mt-1">{heading}</h1>
</div>

<slot />
