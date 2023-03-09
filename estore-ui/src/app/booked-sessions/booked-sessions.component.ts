import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-booked-sessions',
  templateUrl: './booked-sessions.component.html',
  styleUrls: ['./booked-sessions.component.css'],
})
export class BookedSessionsComponent implements OnInit {
  sessions = [
    {
      id: 1,
      author: 'Jakson',
      time: '10/09/2022',
      link: 'https://us05web.zoom.com',
      platform: 'zoom',
      status: 'closed',
    },
    {
      id: 2,
      author: 'John',
      time: '11/09/2022',
      link: 'https://us05web.zoom.com',
      platform: 'zoom',
      status: 'closed',
    },
    {
      id: 3,
      author: 'Mask',
      time: '12/09/2022',
      link: 'https://us05web.zoom.com',
      platform: 'zoom',
      status: 'not opened',
    },
  ];
  displayedColumns: string[] = [
    'id',
    'author',
    'time',
    'link',
    'platform',
    'status',
  ];

  constructor() {
    //constructor
  }

  ngOnInit(): void {
    //does nothing on initialization
  }
}
