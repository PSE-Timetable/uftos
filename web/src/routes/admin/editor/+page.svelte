<script>
  import { goto } from '$app/navigation';
  import { page } from '$app/stores';
  import Editor from '$lib/components/editor.svelte';
  import { Button } from '$lib/elements/ui/button';
  import { ChevronLeft } from 'lucide-svelte';
</script>

<div class="flex flex-row justify-start bg-foreground md:p-4 text-white">
  <Button
    on:click={async () => {
      await goto('/admin');
    }}
    variant="secondary"
    size="icon"
    class="rounded-full bg-accent mr-6"
  >
    <ChevronLeft class="h-5 w-5 text-white" />
  </Button>
  <h1 class="font-bold text-xl mt-1">{$page.data['meta']['title']}</h1>
</div>

<div class="flex justify-center items-center p-4">
  <Editor />
</div>
