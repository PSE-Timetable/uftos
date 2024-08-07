<script lang="ts">
  import { createTable, Render, Subscribe, createRender } from 'svelte-headless-table';
  import * as Table from '$lib/elements/ui/table';
  import DataTableActions from './data-table-actions.svelte';
  import {
    addPagination,
    addSortBy,
    addTableFilter,
    addHiddenColumns,
    addSelectedRows,
    type SortKey,
  } from 'svelte-headless-table/plugins';
  import { Button } from '$lib/elements/ui/button';
  import ChevronDown from 'lucide-svelte/icons/chevron-down';
  import ChevronLeft from 'lucide-svelte/icons/chevron-left';
  import ChevronRight from 'lucide-svelte/icons/chevron-right';
  import * as DropdownMenu from '$lib/elements/ui/dropdown-menu';
  import DataTableCheckbox from './data-table-checkbox.svelte';
  import { ArrowDown, ArrowUp, Plus } from 'lucide-svelte';
  import { Input } from '$lib/elements/ui/input';
  import { writable, type Writable } from 'svelte/store';
  import { onMount } from 'svelte';
  import { goto } from '$app/navigation';
  import { page } from '$app/stores';
  import * as Pagination from '$lib/elements/ui/pagination';
  import type { DataItem } from '$lib/utils/resources';

  onMount(async () => await getData());

  let tableData: Writable<DataItem[]> = writable([]);
  export let columnNames;
  export let keys;
  let totalElements: Writable<number> = writable(0);
  export let loadPage: (
    index: number,
    toSort: string,
    filter: string,
    additionalId?: string,
  ) => Promise<{
    data: DataItem[];
    totalElements: number;
  }>;
  export let deleteEntry: (id: string, additionalId?: string) => Promise<void>;
  export let additionalId: string = '';
  export let sortable = true;
  export let addButton = true;
  export let pageSize = 15;
  export let editAvailable = true;
  let serverSidePagination:boolean = $tableData.length <= pageSize;
  let allItems:DataItem[] = $tableData;

  const table = createTable(tableData, {
    page: addPagination({ serverSide: true, serverItemCount: totalElements, initialPageSize: pageSize }),
    sort: addSortBy({ serverSide: true }),
    filter: addTableFilter({ serverSide: true }),
    hide: addHiddenColumns(),
    select: addSelectedRows(),
  });

  let columns = table.createColumns([
    table.column({
      //first column only contains the checkboxes.
      accessor: (item) => {
        return item[keys[0]];
      },
      id: 'id',
      header: (_, { pluginStates }) => {
        const { allPageRowsSelected } = pluginStates.select;
        return createRender(DataTableCheckbox, {
          checked: allPageRowsSelected,
          white: true,
        });
      },
      cell: ({ row }, { pluginStates }) => {
        const { getRowState } = pluginStates.select;
        const { isSelected } = getRowState(row);

        return createRender(DataTableCheckbox, {
          checked: isSelected,
          white: false,
        });
      },
      plugins: {
        sort: {
          disable: true,
        },
      },
    }),
  ]);
  for (const [i, columnName] of columnNames.entries()) {
    columns = columns.concat(
      table.createColumns([
        table.column({
          accessor: (item: DataItem) => {
            return item[keys[i + 1]];
          },
          header: columnName,
          id: keys[i + 1],
        }),
      ]),
    );
  }

  columns = columns.concat(
    table.createColumns([
      table.column({
        accessor: (item) => {
          return item[keys[0]];
        },
        header: '',
        id: 'actions',
        cell: ({ value }) => {
          return createRender(DataTableActions, {
            id: value.toString(),
            deleteEntry,
            getData,
            additionalId,
            editAvailable,
          });
        },
        plugins: {
          sort: {
            disable: true,
          },
        },
      }),
    ]),
  );

  let tableOptions = {
    rowDataId: (item: { [x: string]: any }, index: number) => {
      return item ? item['id'] : index;
    },
  };
  const { headerRows, pageRows, tableAttrs, tableBodyAttrs, pluginStates, flatColumns, rows } = table.createViewModel(
    columns,
    tableOptions,
  );
  const { pageIndex } = pluginStates.page;
  const { filterValue } = pluginStates.filter;
  const { hiddenColumnIds } = pluginStates.hide;
  const { selectedDataIds, allPageRowsSelected } = pluginStates.select;
  const { sortKeys } = pluginStates.sort;

  const ids = flatColumns.map((col) => col.id);
  let hideForId = Object.fromEntries(ids.map((id) => [id, true]));

  $: $hiddenColumnIds = Object.entries(hideForId)
    .filter(([, hide]) => !hide)
    .map(([id]) => id);

  const hidableCols = keys.slice(1);

  async function getData() {
    if (serverSidePagination) {
      let sortKey: SortKey = $sortKeys[0];
    let sortString;
    sortString = sortKey ? `${sortKey.id},${sortKey.order}` : '';
    let result = await loadPage($pageIndex, sortString, $filterValue, additionalId);
      allItems = result.data;
      
      totalElements.set(result.totalElements);
    }
    tableData.set(allItems.slice($pageIndex * pageSize, $pageIndex * pageSize + pageSize));
    serverSidePagination = allItems.length <= pageSize;
  }

  async function onDeleteKey(e: KeyboardEvent) {
    if (e.code !== 'Delete') {
      return;
    }
    let promises: Promise<void>[] = [];
    Object.keys($selectedDataIds).forEach((row) => {
      promises.push(deleteEntry(row, additionalId));
    });
    await Promise.all(promises);
    $allPageRowsSelected = false;
    await getData();
  }

  $: (async () => {
    $filterValue;
    $pageIndex;
    await getData();
  })();
</script>

<svelte:window on:keydown={onDeleteKey} />
<div class="flex flex-col gap-6">
  <div class="flex items-center">
    <Input
      bind:value={$filterValue}
      class="max-w-sm rounded-none bg-transparent border-0 border-b-2 p-0 border-foreground"
      placeholder="Suche..."
      type="text"
    />
    <DropdownMenu.Root>
      <DropdownMenu.Trigger asChild let:builder>
        <Button builders={[builder]} class="ml-auto shadow-custom text-primary bg-white" variant="secondary">
          Spalten
          <ChevronDown class="ml-2 h-4 w-4" />
        </Button>
      </DropdownMenu.Trigger>
      <DropdownMenu.Content class="text-primary">
        {#each flatColumns as col}
          {#if hidableCols.includes(col.id)}
            <DropdownMenu.CheckboxItem bind:checked={hideForId[col.id]}>
              {col.header}
            </DropdownMenu.CheckboxItem>
          {/if}
        {/each}
      </DropdownMenu.Content>
    </DropdownMenu.Root>
    {#if addButton}
      <Button
        class="ml-auto text-sm text-primary bg-white shadow-custom"
        variant="secondary"
        on:click={() => goto(`${$page.url}/new`)}
        >Hinzufügen
        <Plus class="ml-3" />
      </Button>
    {/if}
  </div>
  <div>
    <Table.Root {...$tableAttrs}>
      <Table.Header class="bg-foreground">
        {#each $headerRows as headerRow}
          <Subscribe rowAttrs={headerRow.attrs()}>
            <Table.Row>
              {#each headerRow.cells as cell (cell.id)}
                <Subscribe attrs={cell.attrs()} let:attrs props={cell.props()} let:props>
                  <Table.Head {...attrs} class="[&:has([role=checkbox])]:pl-4">
                    {#if cell.id !== 'actions' && cell.id !== 'id' && sortable}
                      <Button
                        variant="ghost"
                        on:click={(event) => {
                          props.sort.toggle(event);
                          getData();
                        }}
                        class="text-white"
                      >
                        <Render of={cell.render()} />
                        <div class="flex ml-2">
                          <ArrowUp class="h-4 w-4 {props.sort.order === 'asc' ? 'text-accent' : ''}" />
                          <ArrowDown class="ml-[-4px] h-4 w-4 {props.sort.order === 'desc' ? 'text-accent' : ''}" />
                        </div>
                      </Button>
                    {:else}
                      <div class="text-white">
                        <Render of={cell.render()} />
                      </div>
                    {/if}
                  </Table.Head>
                </Subscribe>
              {/each}
            </Table.Row>
          </Subscribe>
        {/each}
      </Table.Header>
      <Table.Body class="text-primary" {...$tableBodyAttrs}>
        {#each $pageRows as row (row.isData() ? row.dataId : row.id)}
          <Subscribe rowAttrs={row.attrs()} let:rowAttrs>
            <Table.Row
              {...rowAttrs}
              {...rowAttrs}
              data-state={(row.isData() ? $selectedDataIds[row.dataId] : $selectedDataIds[row.id]) && 'selected'}
            >
              {#each row.cells as cell (cell.id)}
                <Subscribe attrs={cell.attrs()} let:attrs>
                  <Table.Cell {...attrs}>
                    <Render of={cell.render()} />
                  </Table.Cell>
                </Subscribe>
              {/each}
            </Table.Row>
          </Subscribe>
        {/each}
      </Table.Body>
    </Table.Root>
  </div>
  <div class="flex justify-center items-center relative">
    <div class=" text-sm text-muted-foreground absolute left-0 items-center">
      {Object.keys($selectedDataIds).length} von{' '}
      {$rows.length} Zeile(n) ausgewählt.
    </div>
    <div>
      <Pagination.Root count={$totalElements} perPage={pageSize} let:pages let:currentPage>
        <Pagination.Content>
          <Pagination.Item>
            <Pagination.PrevButton
              on:click={() => {
                $pageIndex--;
              }}
              class="shadow-custom bg-white"
            >
              <ChevronLeft class="h-4 w-4" />
              <span class="hidden sm:block text-primary">Zurück</span>
            </Pagination.PrevButton>
          </Pagination.Item>
          {#each pages as page (page.key)}
            {#if page.type === 'ellipsis'}
              <Pagination.Item
                on:click={() => {
                  $pageIndex = page;
                }}
              >
                <Pagination.Ellipsis />
              </Pagination.Item>
            {:else if page.value > 0}
              <!--for some reason pages 0 and 1 are displayed if there are no elements in table-->
              <Pagination.Item>
                <Pagination.Link
                  {page}
                  class=" shadow-custom bg-white text-primary {currentPage === page.value
                    ? 'border-2 border-foreground'
                    : ''}"
                  on:click={() => {
                    $pageIndex = page.value - 1;
                  }}
                >
                  {page.value}
                </Pagination.Link>
              </Pagination.Item>
            {/if}
          {/each}
          <Pagination.Item>
            <Pagination.NextButton
              on:click={() => {
                $pageIndex++;
              }}
              class="shadow-custom bg-white"
            >
              <span class="hidden sm:block text-primary">Weiter</span>
              <ChevronRight class="h-4 w-4 stroke-primary" />
            </Pagination.NextButton>
          </Pagination.Item>
        </Pagination.Content>
      </Pagination.Root>
    </div>
  </div>
</div>
