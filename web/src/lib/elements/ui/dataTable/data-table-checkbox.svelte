<script lang="ts">
  import { Checkbox } from '$lib/elements/ui/checkbox';
  import { cn } from '$lib/utils';
  import type { Writable } from 'svelte/store';

  export let checked: Writable<boolean>;
  export let white: boolean;
</script>

{#if white}
  <Checkbox
    bind:checked={$checked}
    class={cn('border-white data-[state=checked]:bg-white data-[state=checked]:text-foreground')}
  />
{:else}
  <Checkbox bind:checked={$checked} />
{/if}
