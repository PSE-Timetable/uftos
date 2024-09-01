<script lang="ts">
  import { toast } from 'svelte-sonner';
  import Button from '$lib/elements/ui/button/button.svelte';
  import { goto } from '$app/navigation';
  import { ChevronLeft } from 'lucide-svelte/icons';
  import LinkBar from '$lib/components/ui/link-bar/link-bar.svelte';
  import * as DropdownMenu from '$lib/elements/ui/dropdown-menu/index.js';
  import { ChevronDown } from 'lucide-svelte';
  import { createTimetable } from '$lib/sdk/fetch-client';

  const settings = [
    { value: 'general_settings', label: 'Allgemeine Einstell.', url: '/admin/settings' },
  ];

  let title: string = '';
  export { title };
  export let backToRoot: boolean = false;
</script>

{#if title !== ''}
  <div class="h-[10%] bg-primary w-fit min-w-full text-white p-4 font-medium text-2xl">
    <Button
      on:click={async () => {
        if (backToRoot) {
          await goto('/');
        } else {
          await goto('../');
        }
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
{:else}
  <div class="bg-primary w-fit min-w-full p-4 gap-4 flex flex-row gap justify-between text-white font-medium text-2xl items-center"> 
    <div class="flex flex-row items-center">
      <Button on:click={() => goto('./')} variant="secondary" size="icon" class="rounded-full bg-accent mr-6">
        <ChevronLeft class="h-5 w-5 text-white" />
      </Button>

      <LinkBar />
    </div>

    <DropdownMenu.Root>
      <DropdownMenu.Trigger asChild let:builder>
        <Button class="bg-white text-primary text-base hover:bg-accent hover:text-white" builders={[builder]}>
          <div class="flex flex-row gap-2 items-center">
            Einstellungen
            <ChevronDown />
          </div>
        </Button>
      </DropdownMenu.Trigger>
      <DropdownMenu.Content class="w-56">
        <DropdownMenu.Group>
          {#each settings as setting}
            <DropdownMenu.Item
              class="text-base"
              on:click={() => (window.location.href = setting.url)}
              >{setting.label}</DropdownMenu.Item
            >
          {/each}
          <DropdownMenu.Item
            class="text-base"
            on:click={async () => {
              const name = `${new Date().getFullYear()}:${Date.now()}`;
              await createTimetable({ name });
              toast.success('Erfolgreich', {
                description: 'Die Erstellung eines Stundenplan wurde erfolgereich gestartet!',
              });
            }}>Stundenplan generieren</DropdownMenu.Item
          >
        </DropdownMenu.Group>
      </DropdownMenu.Content>
    </DropdownMenu.Root>
  </div>
{/if}
