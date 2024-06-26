<script>
    import Checkbox from "$lib/elements/ui/checkbox/checkbox.svelte";
import * as Select from "$lib/elements/ui/select";
    import * as Tabs from "$lib/elements/ui/tabs";


  const classes = [
    { value: "5A", label: "5A" },
    { value: "5B", label: "5B" },
    { value: "5B", label: "5B" },
    { value: "5D", label: "5D" },
    { value: "6A", label: "6A" }
  ];
</script>

<div class="flex flex-row w-full justify-between">
  <Tabs.Root value="account" class="w-[400px]">
  <Tabs.List class="grid w-full grid-cols-2">
    <Tabs.Trigger value="account">Account</Tabs.Trigger>
    <Tabs.Trigger value="password">Password</Tabs.Trigger>
  </Tabs.List>
  <Tabs.Content value="Schüler">
      der
  </Tabs.Content>
  <Tabs.Content value="password">
    
  </Tabs.Content>
</Tabs.Root>

<Select.Root portal={null}>
  <Select.Trigger class="w-[200px]">
    <Select.Value placeholder="Wähle eine Klasse aus" />
  </Select.Trigger>
  <Select.Content>
    <Select.Group>
      {#each classes as classItem}
        <Select.Item value={classItem.value} label={classItem.label}>
          {classItem.label}
        </Select.Item>
      {/each}
    </Select.Group>
  </Select.Content>
  <Select.Input name="favoriteFruit" />
</Select.Root>
</div>
